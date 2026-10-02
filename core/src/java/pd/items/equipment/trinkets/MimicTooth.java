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

public class MimicTooth extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MimicTooth.class)
			.t("name", "拟箱利齿")
			.t("desc", "这颗大尖牙肯定是强行从某个非常倒霉的宝箱怪口中拔下来的。它似乎能影响地牢中的宝箱怪，使它们更常见，也更危险。")
			.t("typical_stats_desc", "这件饰物通常会使所有类型的宝箱怪的出现频率变为原频率的_%1$s倍_，并使宝箱怪更难以识别，而其掉落的战利品也会更加丰厚。此外，每层地牢还会有_%2$s%%_的概率额外带有一个隐藏的黑檀宝箱怪。")
			.t("stats_desc", "在当前等级下，这件饰物会使所有类型的宝箱怪的出现频率变为原频率的_%1$s倍_，并使宝箱怪更难以识别，而其掉落的战利品也会更加丰厚。此外，每层地牢还会有_%2$s%%_的概率额外带有一个隐藏的黑檀宝箱怪。");
	}




	{
		image = EquipmentNonEquipDict.MIMIC_TOOTH_0;
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
					Messages.decimalFormat("#.##", mimicChanceMultiplier(buffedLvl())),
					Messages.decimalFormat("#.##", 100*ebonyMimicChance(buffedLvl())));
		} else {
			return Messages.get(this, "typical_stats_desc",
					Messages.decimalFormat("#.##", mimicChanceMultiplier(0)),
					Messages.decimalFormat("#.##", 100*ebonyMimicChance(0)));
		}
	}

	public static float mimicChanceMultiplier(){
		return mimicChanceMultiplier(trinketLevel(MimicTooth.class));
	}

	public static float mimicChanceMultiplier( int level ){
		if (level == -1){
			return 1f;
		} else {
			return 1.5f + 0.5f*level;
		}
	}

	public static boolean stealthyMimics(){
		return trinketLevel(MimicTooth.class) >= 0;
	}

	public static float ebonyMimicChance(){
		return ebonyMimicChance(trinketLevel(MimicTooth.class));
	}

	public static float ebonyMimicChance( int level ){
		if (level >= 0){
			return 0.125f + 0.125f * level;
		} else {
			return 0;
		}
	}

}
