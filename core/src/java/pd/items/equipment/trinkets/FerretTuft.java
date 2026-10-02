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

//🍋‍🟩
public class FerretTuft extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FerretTuft.class)
			.t("name", "雪貂绒束")
			.t("desc", "一簇银白色的雪貂丝绒，以黄绿色的蝴蝶结捆为一束。雪貂因其敏捷、顽皮与狡黠而闻名。这种力量似乎从这件饰物散发而出，加强了附近任何单位的闪避能力。")
			.t("typical_stats_desc", "这件饰物通常会提升所有单位_%1$s%%_的闪避。")
			.t("stats_desc", "在当前等级下，这件饰物会提升所有单位 _%1$s%%_的闪避。");
	}


	{
		image = EquipmentNonEquipDict.FERRET_TUFT_0;
	}

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 6(12) -> 8(20) -> 10(30)
		return 6+2*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100 * (evasionMultiplier(buffedLvl())-1f)));
		} else {
			return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100 * (evasionMultiplier(0)-1f)));
		}
	}

	public static float evasionMultiplier(){
		return evasionMultiplier(trinketLevel(FerretTuft.class));
	}

	public static float evasionMultiplier(int level ){
		if (level <= -1){
			return 1;
		} else {
			return 1 + 0.125f*(level+1);
		}
	}

}
