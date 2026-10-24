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

package pd.items.consum.food;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.items.Item;

/**
 * SPSEXPD: 魔法果汁——蔬菜 + 水 + 任意果实 的炼金产物（docs/alchemy-rework.md §4）。
 *
 * <p>饮用可回一点饱食度；它是「蒸馏 → 魔法硬糖」的前体（后续批次）。
 */
public class MagicJuice extends Food {

	static {
		InlineText.of(MagicJuice.class)
			.t("name", "魔法果汁")
			.t("desc", "把蔬菜与果实一起榨出的浓稠汁液，带着一点草木的清香。可以直接喝，也能拿去蒸馏成硬糖。");
	}

	{
		//SPSEXPD: 图标先占位，等所有批次完成后统一绘制
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 15f;
		hornValue = 2;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 20 * quantity;
	}
}
