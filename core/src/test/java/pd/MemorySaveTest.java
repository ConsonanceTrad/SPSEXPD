package pd;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import render.utils.Bundle;
import render.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;

/** Uses an isolated temporary directory to verify memory-fire save copying. */
public final class MemorySaveTest {

	public static void main(String[] args) throws Exception {
		Gdx.files = new HeadlessFiles();
		Path temp = java.nio.file.Files.createTempDirectory("sps-memory-save-");
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				temp.toAbsolutePath().toString() + java.io.File.separator);
		try {
			writeBundle(GamesInProgress.gameFile(1), "source-game");
			writeBundle(GamesInProgress.depthFile(1, 7, 0), "source-depth");
			FileUtils.getFileHandle(GamesInProgress.gameFolder(1) + "/note.txt")
					.writeString("not a save", false, "UTF-8");

			Dungeon.copySaveSlotFiles(1, 2);
			check(GamesInProgress.gameExists(2), "目标槽没有game.dat");
			check("source-game".equals(readMarker(GamesInProgress.gameFile(2))), "主存档复制内容错误");
			check("source-depth".equals(readMarker(GamesInProgress.depthFile(2, 7, 0))), "楼层存档复制内容错误");
			check(!FileUtils.fileExists(GamesInProgress.gameFolder(2) + "/note.txt"), "复制了非dat文件");

			boolean overwriteRejected = false;
			try {
				Dungeon.copySaveSlotFiles(1, 2);
			} catch (IOException expected) {
				overwriteRejected = true;
			}
			check(overwriteRejected, "已有槽位未被拒绝");
			check("source-game".equals(readMarker(GamesInProgress.gameFile(2))), "拒绝覆盖时损坏了目标槽");

			writeBundle(GamesInProgress.gameFile(3), "broken-source");
			FileUtils.getFileHandle(GamesInProgress.depthFile(3, 9, 0))
					.writeString("broken bundle", false, "UTF-8");
			boolean corruptRejected = false;
			try {
				Dungeon.copySaveSlotFiles(3, 4);
			} catch (IOException expected) {
				corruptRejected = true;
			}
			check(corruptRejected, "损坏的源存档未被拒绝");
			check(!FileUtils.dirExists(GamesInProgress.gameFolder(4)), "失败后遗留了不完整目标槽");

			System.out.println("SPS记忆存档测试通过：隔离目录复制、拒绝覆盖和坏档清理均正常。");
		} finally {
			try (java.util.stream.Stream<Path> paths = java.nio.file.Files.walk(temp)) {
				paths.sorted(Comparator.reverseOrder()).forEach(path -> {
					try { java.nio.file.Files.deleteIfExists(path); } catch (IOException ignored) { }
				});
			}
		}
	}

	private static void writeBundle(String path, String marker) throws IOException {
		Bundle bundle = new Bundle();
		bundle.put("marker", marker);
		FileUtils.bundleToFile(path, bundle);
	}

	private static String readMarker(String path) throws IOException {
		return FileUtils.bundleFromFile(path).getString("marker");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private MemorySaveTest() { }
}
