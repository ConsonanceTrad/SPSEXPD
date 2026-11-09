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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pd.items.unique.items;

import pd.atlas.items.ConsumUsefulProcessEnhanceDict;
import pd.items.Item;
import pd.messages.InlineText;

/**
 * SPSEXPD: 青魔法笔记 No.1（批次F占位）——青魔法体系的载体道具，机制延后实装。
 * 当前无任何动作与效果；图标沿用精通书页占位，待画专属贴图。
 */
public class BlueMagicNotes extends Item {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(BlueMagicNotes.class)
			.t("name", "青魔法笔记 No.1")
			.t("desc", "一册速记原典，墨色泛青。它讲述的是从怪物身上习得它们的特殊法术的技艺——\n\n但这一册还是空白，仪式与施法的学问尚未写到这一章。");
	}

	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
		stackable = false;
		unique = true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
}
