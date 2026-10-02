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
import render.utils.math.Random;
import pd.messages.InlineText;

public class ThirteenLeafClover extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ThirteenLeafClover.class)
			.t("name", "十三叶草")
			.t("desc", "不知为何，在炼金釜中烹煮竟让这株三叶草长出了许多额外的叶子！目前尚不清楚这件饰物会带来好运还是厄运，或许它会让你的运气变得更加混沌无常？")
			.t("typical_stats_desc", "这件饰物通常会使你有_%1$d%%_的概率造成最大伤害，而有_%2$d%%_的概率造成最小伤害。")
			.t("stats_desc", "在当前等级下，这件饰物会使你有_%1$d%%_的概率造成最大伤害，而有_%2$d%%_的概率造成最小伤害。");
	}




	{
		image = EquipmentNonEquipDict.CLOVER_0;
	}

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 6(12) -> 8(20) -> 10(30)
		return 6+2*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this, "stats_desc", Math.round(MAX_CHANCE * 100*alterHeroDamageChance(buffedLvl())), Math.round((1f-MAX_CHANCE) * 100*alterHeroDamageChance(buffedLvl())));
		} else {
			return Messages.get(this, "typical_stats_desc", Math.round(MAX_CHANCE * 100*alterHeroDamageChance(0)), Math.round((1f-MAX_CHANCE) * 100*alterHeroDamageChance(0)));
		}
	}

	public static float alterHeroDamageChance(){
		return alterHeroDamageChance(trinketLevel(ThirteenLeafClover.class));
	}

	public static float alterHeroDamageChance(int level ){
		if (level <= -1){
			return 0;
		} else {
			return 0.25f + 0.25f*level;
		}
	}

	private static float MAX_CHANCE = 0.6f;

	public static int alterDamageRoll(int min, int max){
		if (Random.Float() < MAX_CHANCE){
			return max;
		} else {
			return min;
		}
	}

}
