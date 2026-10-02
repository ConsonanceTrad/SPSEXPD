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

import pd.atlas.items.ConsumGoodsMaterialsMaterialsDict;

import pd.items.Item;
import pd.messages.InlineText;

public class ImpStatue extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ImpStatue.class)
			.t("name", "黑曜石雕像")
			.t("desc", "一块小小的漆黑雕塑，由黑曜石雕成。它的外表与委托你进入宝库的那位小恶魔神似，连那闪亮的绿眼睛也颇为相像。雕像的底座上用你没有见过的语言刻着一串铭文。")
			.t("discover_hint", "你可在某个任务中找到该物品。");
	}


	{
		image = ConsumGoodsMaterialsMaterialsDict.STATUE_0;

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
