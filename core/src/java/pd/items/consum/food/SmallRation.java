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

import pd.actors.buffs.Hunger;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class SmallRation extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SmallRation.class)
			.t("name", "小包口粮")
			.t("eat_msg", "吃起来还行。")
			.t("desc", "它看起来和普通口粮一样，就是小了点。")
			.t("discover_hint", "你可在商店中购买该物品。");
	}




	{
		image = ConsumFoodFoodDict.SMALL_RATION_PACK;
		energy = 150f; //SPSEXPD: 固定绝对值（原 Hunger.HUNGRY/2f）
	}
	
	@Override
	public int value() {
		return 10 * quantity;
	}
}
