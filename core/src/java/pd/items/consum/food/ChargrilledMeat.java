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

public class ChargrilledMeat extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ChargrilledMeat.class)
			.t("name", "烤肉")
			.t("desc", "看起来像块好肉排。")
			.t("discover_hint", "你可使用另一种食物制作该物品。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = Hunger.HUNGRY/2f;
	}
	
	@Override
	public int value() {
		return 8 * quantity;
	}
	
	public static Food cook( int quantity ) {
		ChargrilledMeat result = new ChargrilledMeat();
		result.quantity = quantity;
		return result;
	}
}
