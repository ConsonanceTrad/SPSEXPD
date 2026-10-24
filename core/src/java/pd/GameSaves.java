/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import pd.messages.Messages;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

/**
 * SPSEXPD: 存档导出 / 导入（docs/alchemy-rework.md §9）。
 *
 * <p>把整个存档槽（`gameN/game.dat` + 所有 `depth*.dat`）打包成**单个 JSON 文件**，
 * 每个文件以 base64 原样保存，因此与存档内部格式（含 gzip）无关。
 *
 * <p>导出位置：桌面端由玩家手输路径；安卓端写到系统的 Download 目录
 * （写不进去就回退到应用私有目录 `exports/`）。导入只会写进**空槽**，不会覆盖已有存档。
 */
public final class GameSaves {

	//SPSEXPD: 简体中文内联文案
	static {
		pd.messages.InlineText.of(GameSaves.class)
				.t("no_save", "这个存档槽里没有可导出的存档。")
				.t("no_slot", "没有空闲的存档槽，请先删除一个存档。")
				.t("bad_path", "路径无效。")
				.t("bad_file", "这不是有效的存档导出文件。")
				.t("failed", "存档操作失败，请检查路径与存储权限。");
	}

	private GameSaves() {}

	private static final String FORMAT = "format";
	private static final String SLOT = "slot";
	private static final String EXPORTED = "exported";
	private static final String FILES = "files";
	private static final String GAME_FILE = "game.dat";
	private static final int FORMAT_VERSION = 1;

	/** 文件名的公共前缀，便于在 Download 目录里筛出本游戏的导出文件。 */
	public static final String EXPORT_PREFIX = "SPSEXPD-";

	private static String message(String key) {
		return Messages.get(GameSaves.class, key);
	}

	public static boolean isAndroid() {
		return Gdx.app != null && Gdx.app.getType() == Application.ApplicationType.Android;
	}

	//*** 路径

	/** 安卓端的导出目录（系统 Download）。 */
	private static FileHandle androidDownloadDir() {
		return Gdx.files.external("Download/");
	}

	/** 默认导出文件名。 */
	public static String defaultExportName(int slot) {
		return EXPORT_PREFIX + "slot" + slot + "-" + System.currentTimeMillis() + ".json";
	}

	/** 桌面端建议的完整路径（玩家可在输入框里改）。 */
	public static String suggestDesktopPath(int slot) {
		FileHandle local = Gdx.files.local(defaultExportName(slot));
		String path = local.file().getAbsolutePath();
		return path;
	}

	/** 把玩家输入的路径解析成可写的 FileHandle。 */
	private static FileHandle resolveOutput(String path) {
		String trimmed = path == null ? "" : path.trim();
		if (trimmed.isEmpty()) return null;
		//安卓端统一写 Download（相对名由调用方给出）
		if (isAndroid() && !trimmed.startsWith("/")) {
			trimmed = "Download/" + trimmed;
		}
		boolean absolute = trimmed.startsWith("/") || trimmed.matches("^[A-Za-z]:.*");
		if (absolute) {
			return Gdx.files.absolute(trimmed);
		}
		return isAndroid() ? Gdx.files.external(trimmed) : Gdx.files.local(trimmed);
	}

	//*** 导出

	/**
	 * 导出一个存档槽到 JSON 文件。
	 *
	 * @param slot 存档槽（从 1 开始）
	 * @param path 目标路径（桌面 = 绝对/相对路径；安卓 = Download 下的文件名）
	 * @return 实际写入的路径（用于提示）
	 */
	public static String exportSlot(int slot, String path) throws IOException {
		String folder = GamesInProgress.gameFolder(slot);
		if (!FileUtils.dirExists(folder)) {
			throw new IOException(message("no_save"));
		}

		JSONObject files = new JSONObject();
		JSONObject root = new JSONObject();
		int count = 0;
		try {
			for (String name : FileUtils.filesInDir(folder)) {
				if (!name.endsWith(".dat")) continue;
				byte[] data = FileUtils.getFileHandle(folder + "/" + name).readBytes();
				if (data == null || data.length == 0) continue;
				files.put(name, new String(Base64Coder.encode(data)));
				count++;
			}
			if (count == 0) {
				throw new IOException(message("no_save"));
			}
			root.put(FORMAT, FORMAT_VERSION);
			root.put(SLOT, slot);
			root.put(EXPORTED, System.currentTimeMillis());
			root.put(FILES, files);
		} catch (JSONException e) {
			throw new IOException(message("failed"), e);
		}

		FileHandle out = resolveOutput(path);
		if (out == null) throw new IOException(message("bad_path"));
		try {
			out.writeString(root.toString(), false, "UTF-8");
		} catch (RuntimeException e) {
			//安卓 Download 不可写时回退到应用私有目录
			FileHandle fallback = Gdx.files.local("exports/" + defaultExportName(slot));
			try {
				fallback.writeString(root.toString(), false, "UTF-8");
				return fallback.file().getAbsolutePath();
			} catch (RuntimeException ignored) {
				throw new IOException(message("failed"), e);
			}
		}
		return out.file().getAbsolutePath();
	}

	//*** 导入

	/**
	 * 把 JSON 文件导入到一个**空存档槽**。
	 *
	 * @return 导入到的槽位
	 */
	public static int importIntoEmptySlot(String path) throws IOException {
		FileHandle in = resolveOutput(path);
		if (in == null || !in.exists() || in.isDirectory()) {
			throw new IOException(message("bad_path"));
		}

		String text;
		try {
			text = in.readString("UTF-8");
		} catch (RuntimeException e) {
			throw new IOException(message("failed"), e);
		}

		JSONObject files;
		try {
			JSONObject root = new JSONObject(text);
			files = root.optJSONObject(FILES);
			if (files == null) throw new IOException(message("bad_file"));
		} catch (JSONException e) {
			throw new IOException(message("bad_file"), e);
		}

		//必须先确认里面确实有主存档，再动磁盘
		String gameData = files.optString(GAME_FILE, "");
		if (gameData.isEmpty()) throw new IOException(message("bad_file"));

		byte[] gameBytes;
		try {
			gameBytes = Base64Coder.decode(gameData);
		} catch (IllegalArgumentException e) {
			throw new IOException(message("bad_file"), e);
		}
		//能按存档格式解析才继续
		try {
			Bundle.read(new ByteArrayInputStream(gameBytes));
		} catch (Exception e) {
			throw new IOException(message("bad_file"), e);
		}

		int slot = GamesInProgress.firstEmpty();
		if (slot < 1) throw new IOException(message("no_slot"));

		String folder = GamesInProgress.gameFolder(slot);
		//清掉目标槽可能残留的旧楼层缓存
		for (String name : FileUtils.filesInDir(folder)) {
			if (name.endsWith(".dat")) {
				FileUtils.deleteFile(folder + "/" + name);
			}
		}

		try {
			Iterator<String> keys = files.keys();
			while (keys.hasNext()) {
				String name = keys.next();
				if (!name.endsWith(".dat")) continue;
				byte[] data;
				try {
					data = Base64Coder.decode(files.getString(name));
				} catch (IllegalArgumentException | JSONException e) {
					continue;
				}
				FileUtils.getFileHandle(folder + "/" + name).writeBytes(data, false);
			}
		} catch (RuntimeException e) {
			throw new IOException(message("failed"), e);
		}

		GamesInProgress.setUnknown(slot);
		return slot;
	}

	/** 安卓端：列出 Download 目录里可导入的导出文件（桌面返回空列表，改用手输路径）。 */
	public static ArrayList<String> listImportableNames() {
		ArrayList<String> result = new ArrayList<>();
		if (!isAndroid()) return result;
		try {
			FileHandle dir = androidDownloadDir();
			if (dir == null || !dir.exists() || !dir.isDirectory()) return result;
			for (FileHandle f : dir.list()) {
				if (f != null && !f.isDirectory()
						&& f.name().startsWith(EXPORT_PREFIX) && f.name().endsWith(".json")) {
					result.add(f.name());
				}
			}
		} catch (RuntimeException ignored) {
			//无权限等情况直接返回空列表
		}
		Collections.sort(result);
		return result;
	}
}
