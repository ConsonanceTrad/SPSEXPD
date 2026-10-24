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

/**
 * SPSEXPD: 魔法硬糖——魔法果汁经「蒸馏压片机」蒸馏后的产物（docs/alchemy-rework.md §4）。
 */
public class MagicCandy extends Food {

	static {
		InlineText.of(MagicCandy.class)
			.t("name", "魔法硬糖")
			.t("desc", "浓缩到几乎发亮的果蔬硬糖，含在嘴里能持续补充体力。");
	}

	{
		//SPSEXPD: 图标先占位，等所有批次完成后统一绘制
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 30f;
		hornValue = 3;
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
		return 30 * quantity;
	}
}
