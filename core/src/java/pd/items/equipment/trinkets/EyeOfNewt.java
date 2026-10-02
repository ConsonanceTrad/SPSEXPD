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

public class EyeOfNewt extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EyeOfNewt.class)
			.t("name", "蝾螈魔眼")
			.t("desc", "这颗黑金色的蝾螈之眼是一种常见的炼金原料，而现在已被附魔强化为一件饰物。魔眼似乎降低了你的视力，以为你换取其他形式的视觉。")
			.t("typical_stats_desc", "这件饰物通常会降低你_%1$s%%_的视距，但也会使你获得对_%2$d_格范围内敌人的灵视感知。")
			.t("stats_desc", "在当前等级下，这件饰物会降低你_%1$s%%_的视距，但也会使你获得对_%2$d_格范围内敌人的灵视感知。");
	}




	{
		image = EquipmentNonEquipDict.EYE_OF_NEWT_0;
	}

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 6(12) -> 8(20) -> 10(30)
		return 6+2*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this, "stats_desc",
					Messages.decimalFormat("#.##", 100*(1f-visionRangeMultiplier(buffedLvl()))),
					mindVisionRange(buffedLvl()));
		} else {
			return Messages.get(this, "typical_stats_desc",
					Messages.decimalFormat("#.##", 100*(1f-visionRangeMultiplier(0))),
					mindVisionRange(0));
		}
	}

	public static float visionRangeMultiplier(){
		return visionRangeMultiplier(trinketLevel(EyeOfNewt.class));
	}

	public static float visionRangeMultiplier( int level ){
		if (level < 0){
			return 1;
		} else {
			return 0.875f - 0.125f*level;
		}
	}

	public static int mindVisionRange(){
		return mindVisionRange(trinketLevel(EyeOfNewt.class));
	}

	public static int mindVisionRange( int level ){
		if (level < 0){
			return 0;
		} else {
			return 2+level;
		}
	}

}
