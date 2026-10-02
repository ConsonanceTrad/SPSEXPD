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

package pd.items.quest;

import pd.atlas.items.SpecificTaskDict;

import pd.items.Item;
import pd.messages.InlineText;

public class DarkGold extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DarkGold.class)
			.t("name", "暗金矿")
			.t("you_now_have", "你现在拥有%d个暗金矿。")
			.t("desc", "这种金属名中的暗并非源于其色泽(它看起来和普通黄金一样)，而是因为它会在阳光下熔化，令其在地表上毫无用处。")
			.t("discover_hint", "你可在某个任务中找到该物品。");
	}



	
	{
		image = SpecificTaskDict.ORE_0;
		
		stackable = true;
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
