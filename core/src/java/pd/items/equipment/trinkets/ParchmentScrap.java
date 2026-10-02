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

public class ParchmentScrap extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ParchmentScrap.class)
			.t("name", "残魔余卷")
			.t("desc", "这块小小的羊皮纸残片看起来来自一张卷轴。它仍附有一丝残存的魔力，并且似乎能影响在地牢中找到的武器和护甲。")
			.t("typical_stats_desc", "这件饰物通常会使附魔与刻印的出现频率变为原频率的_%d倍_，而武器与护甲的诅咒的出现频率变为原频率的_%s倍_。法杖、戒指与神器的诅咒不受影响。\n\n这件饰物升级所消耗的炼金能量较多。")
			.t("stats_desc", "在当前等级下，这件饰物会使附魔与刻印的出现频率变为原频率的_%d倍_，而武器与护甲的诅咒的出现频率变为原频率的_%s倍_。法杖、戒指与神器的诅咒不受影响。\n\n这件饰物升级所消耗的炼金能量较多。");
	}


	{
		image = EquipmentNonEquipDict.PARCHMENT_SCRAP_0;
	}

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 10(16) -> 15(31) -> 20(51)
		return 10+5*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this, "stats_desc", (int)enchantChanceMultiplier(buffedLvl()), Messages.decimalFormat("#.##", curseChanceMultiplier(buffedLvl())));
		} else {
			return Messages.get(this, "typical_stats_desc", (int)enchantChanceMultiplier(0), Messages.decimalFormat("#.##", curseChanceMultiplier(0)));
		}
	}

	public static float enchantChanceMultiplier(){
		return enchantChanceMultiplier(trinketLevel(ParchmentScrap.class));
	}

	public static float enchantChanceMultiplier( int level ){
		switch (level){
			default:
				return 1;
			case 0:
				return 2;
			case 1:
				return 4;
			case 2:
				return 7;
			case 3:
				return 10;
		}
	}

	public static float curseChanceMultiplier(){
		return curseChanceMultiplier(trinketLevel(ParchmentScrap.class));
	}

	public static float curseChanceMultiplier( int level ){
		switch (level){
			default:
				return 1;
			case 0:
				return 1.5f;
			case 1:
				return 2f;
			case 2:
				return 1f;
			case 3:
				return 0f;
		}
	}
}
