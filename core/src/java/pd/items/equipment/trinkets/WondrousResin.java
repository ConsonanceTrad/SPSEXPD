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

public class WondrousResin extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WondrousResin.class)
			.t("name", "奇迹树脂")
			.t("desc", "这团泛着微光的蓝色树脂看起来附有某根诅咒法杖魔法的纯化精华。炼金釜中的魔力似乎在一定程度上稳定了树脂的诅咒魔法，而这种魔法现在正影响着你的法杖。")
			.t("typical_stats_desc", "这件饰物通常会有_%1$s%%_的概率迫使诅咒法杖效果变得无害或有益，还会有_%2$s%%_的概率使无诅咒的法杖施放一次额外的无害或有益的诅咒法杖效果。\n\n这件饰物升级所消耗的炼金能量较多。")
			.t("stats_desc", "在当前等级下，这件饰物会有_%1$s%%_的概率迫使诅咒法杖效果变得无害或有益，还会有_%2$s%%_的概率使无诅咒的法杖施放一次额外的无害或有益的诅咒法杖效果。\n\n这件饰物升级所消耗的炼金能量较多。");
	}


	{
		image = EquipmentNonEquipDict.WONDROUS_RESIN_0;
	}

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 10(16) -> 15(31) -> 20(51)
		return 10+5*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this, "stats_desc",
					Messages.decimalFormat("#.##", 100*positiveCurseEffectChance(buffedLvl())),
					Messages.decimalFormat("#.##", 100*extraCurseEffectChance(buffedLvl())));
		} else {
			return Messages.get(this, "typical_stats_desc",
					Messages.decimalFormat("#.##", 100*positiveCurseEffectChance(0)),
					Messages.decimalFormat("#.##", 100*extraCurseEffectChance(0)));
		}
	}

	//used when bonus curse effects are being created
	public static boolean forcePositive = false;

	public static float positiveCurseEffectChance(){
		if (forcePositive){
			return 1;
		}
		return positiveCurseEffectChance( trinketLevel(WondrousResin.class) );
	}

	public static float positiveCurseEffectChance(int level ){
		if (level >= 0){
			return 0.25f + 0.25f * level;
		} else {
			return 0;
		}
	}

	public static float extraCurseEffectChance(){
		return extraCurseEffectChance( trinketLevel(WondrousResin.class) );
	}

	public static float extraCurseEffectChance( int level ){
		if (level >= 0){
			return 0.125f + 0.125f * level;
		} else {
			return 0;
		}
	}

}
