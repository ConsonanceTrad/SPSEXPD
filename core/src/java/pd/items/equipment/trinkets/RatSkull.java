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

import pd.atlas.items.ConsumGoodsMaterialsGoodsDict;

import pd.messages.Messages;
import pd.messages.InlineText;

public class RatSkull extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RatSkull.class)
			.t("name", "巨鼠头骨")
			.t("desc", "这件可怕的饰物并不比正常老鼠的头骨大多少，但不知为何在这个地牢却很稀有。头骨的魔力似乎能吸引更多稀有的地牢住民，使它们更有可能出现。")
			.t("typical_stats_desc", "这件饰物通常会使稀有敌人的出现频率变为原频率的_%d倍_。然而，头骨对水晶宝箱怪与装甲石像效果减半。")
			.t("stats_desc", "在当前等级下，这件饰物会使稀有敌人的出现频率变为原频率的_%d倍_。然而，头骨对水晶宝箱怪与装甲石像效果减半。");
	}


	{
		image = ConsumGoodsMaterialsGoodsDict.RAT_SKULL_0;
	}

	@Override
	protected int upgradeEnergyCost() {
		//6 -> 6(12) -> 8(20) -> 10(30)
		return 6+2*level();
	}

	@Override
	public String statsDesc() {
		if (isIdentified()){
			return Messages.get(this, "stats_desc", (int)(exoticChanceMultiplier(buffedLvl())));
		} else {
			return Messages.get(this, "typical_stats_desc", (int)(exoticChanceMultiplier(0)));
		}
	}

	public static float exoticChanceMultiplier(){
		return exoticChanceMultiplier(trinketLevel(RatSkull.class));
	}

	public static float exoticChanceMultiplier( int level ){
		if (level == -1){
			return 1f;
		} else {
			return 2f + 1f*level;
		}
	}

}
