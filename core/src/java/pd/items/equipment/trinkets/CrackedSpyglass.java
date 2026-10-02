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

public class CrackedSpyglass extends Trinket{
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CrackedSpyglass.class)
			.t("name", "幻象裂镜")
			.t("desc", "若是其前镜头完好无损，则这柄手持式望远镜可称得上一件能工巧匠的伟大之作了。望远镜似乎在为你揭示地牢中的新物品，但由于其本身的缺陷，这些被揭示的物品并不容易被看清。")
			.t("typical_stats_desc", "这件饰物通常会有_%1$s%%_的概率在除Boss层以外的每层额外生成一件隐藏物品。")
			.t("stats_desc", "在当前等级下，这件饰物会有_%1$s%%_的概率在除Boss层以外的每层额外生成一件隐藏物品。")
			.t("stats_desc_upgraded", "在当前等级下，这件饰物会有_100%%_的概率在除Boss层以外的每层额外生成一件隐藏物品，并有_%1$s%%_的概率再额外生成一件隐藏物品。");
	}


	{
		image = EquipmentNonEquipDict.SPYGLASS_0;
	}

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 6(12) -> 8(20) -> 10(30)
		return 6+2*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			if (buffedLvl() >= 2){
				return Messages.get(this, "stats_desc_upgraded", Messages.decimalFormat("#.##", 100 * (extraLootChance(buffedLvl())-1f)));
			} else {
				return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100 * extraLootChance(buffedLvl())));
			}
		} else {
			return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100 * extraLootChance(0)));
		}
	}

	public static float extraLootChance(){
		return extraLootChance(trinketLevel(CrackedSpyglass.class));
	}

	public static float extraLootChance(int level ){
		if (level <= -1){
			return 0;
		} else {
			return 0.375f*(level+1);
		}
	}

}
