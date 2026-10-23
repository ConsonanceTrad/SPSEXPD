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

package pd.items;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;

/**
 * SPSEXPD: 读取 {@code assets/data/alchemy.csv} 的炼金公式表。
 *
 * <p>表格是运行期真源，方便直接做平衡调整；读取失败（无头环境、文件缺失、解析全失败）
 * 时由 {@link SpsBrewRecipes#all()} 回退到内置公式，保证游戏不会因缺表而崩溃。
 *
 * <p>表格式说明见 {@code docs/alchemy-rework.md} §7。
 */
public final class AlchemyCsv {

	public static final String PATH = "data/alchemy.csv";

	private AlchemyCsv() {}

	/** 读取并解析公式表；任何环节失败都返回空列表（调用方回退内置表）。 */
	public static List<BrewRecipe> load() {
		String text = read();
		if (text == null) return new ArrayList<>();
		try {
			List<BrewRecipe> recipes = TableBrewRecipe.parse(text);
			return recipes == null ? new ArrayList<>() : recipes;
		} catch (RuntimeException e) {
			return new ArrayList<>();
		}
	}

	private static String read() {
		try {
			if (Gdx.files == null) return null;
			if (!Gdx.files.internal(PATH).exists()) return null;
			return Gdx.files.internal(PATH).readString("UTF-8");
		} catch (Throwable t) {
			//无头校验/测试环境没有文件系统后端：回退内置表
			return null;
		}
	}
}
