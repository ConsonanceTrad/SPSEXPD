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

public class PetrifiedSeed extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PetrifiedSeed.class)
			.t("name", "石化种子")
			.t("desc", "这粒种子在缓慢的地质作用或法术作用的影响下石化了。它似乎通过魔法影响着地牢的植物群系，时不时使种子转化为符石。")
			.t("typical_stats_desc", "这件饰物通常会有_%1$s%%_的概率使被践踏的高草掉落符石而非种子，还会使高草掉落物品的概率提升_%2$s%%_。")
			.t("stats_desc", "在当前等级下，这件饰物会有_%1$s%%_的概率使被践踏的高草掉落符石而非种子，还会使高草掉落物品的概率提升_%2$s%%_。");
	}


	{
		image = EquipmentNonEquipDict.PETRIFIED_SEED_0;
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
					Messages.decimalFormat("#.##", 100*stoneInsteadOfSeedChance(buffedLvl())),
					Messages.decimalFormat("#.##", 100*(grassLootMultiplier(buffedLvl())-1f)));
		} else {
			return Messages.get(this, "typical_stats_desc",
					Messages.decimalFormat("#.##", 100*stoneInsteadOfSeedChance(0)),
					Messages.decimalFormat("#.##", 100*(grassLootMultiplier(0)-1f)));
		}
	}

	public static float grassLootMultiplier(){
		return grassLootMultiplier(trinketLevel(PetrifiedSeed.class));
	}

	public static float grassLootMultiplier( int level ){
		if (level <= 0){
			return 1f;
		} else {
			return 1f + .25f*level/3f;
		}
	}

	public static float stoneInsteadOfSeedChance(){
		return stoneInsteadOfSeedChance(trinketLevel(PetrifiedSeed.class));
	}

	//when accounting for boosts, we effectively get:
	//stones: 25/50/75/100%
	//seeds:  75/58/38/25%
	public static float stoneInsteadOfSeedChance( int level ){
		switch (level){
			default:
				return 0;
			case 0:
				return 0.25f;
			case 1:
				return 0.46f;
			case 2:
				return 0.65f;
			case 3:
				return 0.8f;
		}
	}
}
