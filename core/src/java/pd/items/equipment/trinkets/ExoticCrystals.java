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

package pd.items.equipment.trinkets;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.messages.Messages;
import pd.messages.InlineText;

public class ExoticCrystals extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ExoticCrystals.class)
			.t("name", "奇异能晶")
			.t("desc", "这些小型粉色晶体有着和炼金能量晶体一样的几何外形。尽管它们不能直接为炼金实验供能，但不知为何似乎能影响你找到的药剂和卷轴。")
			.t("typical_stats_desc", "这件饰物通常会使_%s%%_的药剂、卷轴转化为其对应的合剂、秘卷。转化不会影响力量药剂，升级卷轴与为解决特殊房间提供帮助而生成的物品。")
			.t("stats_desc", "在当前等级下，这件饰物会使_%s%%_的药剂、卷轴转化为其对应的合剂、秘卷。转化不会影响力量药剂，升级卷轴与为解决特殊房间提供帮助而生成的物品。");
	}


	{
		image = EquipmentNonEquipDict.EXOTIC_CRYSTALS_0;
	}

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 6(12) -> 8(20) -> 10(30)
		return 6+2*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100*consumableExoticChance(buffedLvl())));
		} else {
			return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100*consumableExoticChance(0)));
		}
	}

	public static float consumableExoticChance(){
		return consumableExoticChance(trinketLevel(ExoticCrystals.class));
	}

	public static float consumableExoticChance( int level ){
		if (level == -1){
			return 0f;
		} else {
			return 0.2f + 0.2f*level;
		}
	}

}
