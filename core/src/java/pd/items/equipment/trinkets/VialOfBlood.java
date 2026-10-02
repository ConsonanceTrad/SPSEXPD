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

import pd.Dungeon;
import pd.messages.Messages;
import pd.messages.InlineText;

public class VialOfBlood extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(VialOfBlood.class)
			.t("name", "凝血试管")
			.t("desc", "这根细长的试管内装有一些地牢住民的血液，当你转动试管时其中的血液也会随之缓慢流动。它似乎通过魔法使治疗更为强效，但也更为缓效。")
			.t("typical_stats_desc", "这件饰物通常会提升你从治疗药剂、水袋或生命之泉获得的治疗总量_%1$s%%_。但治疗速度也会更为迟滞，每回合最多回复_%2$s点_生命值(此数值会随着英雄等级的提升而提升)。")
			.t("stats_desc", "在当前等级下，这件饰物会提升你从治疗药剂、水袋或生命之泉获得的治疗总量_%1$s%%_。但治疗速度也会更为迟滞，每回合最多回复_%2$s点_生命值(此数值会随着英雄等级的提升而提升)。");
	}


	{
		image = EquipmentNonEquipDict.BLOOD_VIAL_0;
	}

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 6(12) -> 8(20) -> 10(30)
		return 6+2*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this,
					"stats_desc",
					Messages.decimalFormat("#.##", 100*(totalHealMultiplier(buffedLvl())-1f)),
					Integer.toString(maxHealPerTurn(buffedLvl())));
		} else {
			return Messages.get(this,
					"typical_stats_desc",
					Messages.decimalFormat("#.##", 100*(totalHealMultiplier(0)-1f)),
					Integer.toString(maxHealPerTurn(0)));
		}
	}

	public static boolean delayBurstHealing(){
		return trinketLevel(VialOfBlood.class) != -1;
	}

	public static int bloodVialLevel(){
		return trinketLevel(VialOfBlood.class);
	}

	public static float totalHealMultiplier(){
		return totalHealMultiplier(trinketLevel(VialOfBlood.class));
	}

	public static float totalHealMultiplier(int level){
		if (level == -1){
			return 1;
		} else {
			return 1f + 0.125f*(level+1);
		}
	}

	public static int maxHealPerTurn(){
		return maxHealPerTurn(trinketLevel(VialOfBlood.class));
	}

	public static int maxHealPerTurn(int level){
		int maxHP = Dungeon.hero == null ? 20 : Dungeon.hero.HT;
		if (level == -1){
			return maxHP;
		} else {
			switch (level){
				case 0: default:
					return 4 + Math.round(0.15f*maxHP);
				case 1:
					return 3 + Math.round(0.10f*maxHP);
				case 2:
					return 2 + Math.round(0.07f*maxHP);
				case 3:
					return 1 + Math.round(0.05f*maxHP);
			}
		}
	}

}
