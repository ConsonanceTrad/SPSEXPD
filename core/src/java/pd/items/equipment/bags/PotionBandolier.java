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

package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.LiquidMetal;
import pd.items.consum.potions.Potion;
import pd.messages.InlineText;

public class PotionBandolier extends Bag {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionBandolier.class)
			.t("name", "药剂挎带")
			.t("desc", "这副厚实的挎带能像肩带一样缠在身上，上面有许多用来放药剂和液金的隔热皮带。\n\n挎带应该能为存放其中的药剂抵御寒冷。");
	}




	{
		image = EquipmentBagsDict.BANDOLIER;
	}

	@Override
	public boolean canHold( Item item ) {
		//SPSEXPD: 露珠瓶不再进药剂挎带，只被主背包收录
		if (item instanceof Potion || item instanceof LiquidMetal){
			return super.canHold(item);
		} else {
			return false;
		}
	}

	/** SPSEXPD: 标签页固定排序位。 */
	@Override public int bagOrder() { return 2; }

	//SPSEXPD: 容量 34 = 35-1。窗口里包裹本体自身还占一格，两者相加须正好占满整数行
	//（5 列 x 7 行 = 7 列 x 5 行 = 35），否则装满时会多出一行空行。曾为 35
	public int capacity(){
		return 34;
	}

	@Override
	public int value() {
		return 40;
	}

}
