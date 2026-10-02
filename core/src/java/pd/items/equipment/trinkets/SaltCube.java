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

public class SaltCube extends Trinket {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SaltCube.class)
			.t("name", "盐晶立方")
			.t("desc", "这块巨大的盐晶被切割成近乎完美的立方体，而不知为何盐晶成功吸收了炼金釜中一半的水分而非溶解于其中。它似乎通过魔法脱水并保存了你所吃的食物，延长了你所得的饱腹感，但也减少了你不空腹时的生命回复。")
			.t("typical_stats_desc", "这件饰物通常会增加你_%1$s%%_的饥饿所需时间，但也会降低你_%2$s%%_的生命回复速率，若楼层已被封锁则上述效果无效。")
			.t("stats_desc", "在当前等级下，这件饰物会增加你_%1$s%%_的饥饿所需时间，但也会降低你_%2$s%%_的生命回复速率，若楼层已被封锁则上述效果无效。");
	}




	{
		image = EquipmentNonEquipDict.SALT_CUBE_0;
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
					Messages.decimalFormat("#.##", 100*((1f/hungerGainMultiplier(buffedLvl()))-1f)),
					Messages.decimalFormat("#.##", 100*(1f-healthRegenMultiplier(buffedLvl()))));
		} else {
			return Messages.get(this,
					"typical_stats_desc",
					Messages.decimalFormat("#.##", 100*((1f/hungerGainMultiplier(0))-1f)),
					Messages.decimalFormat("#.##", 100*(1f-healthRegenMultiplier(0))));
		}
	}

	public static float hungerGainMultiplier(){
		return hungerGainMultiplier(trinketLevel(SaltCube.class));
	}

	public static float hungerGainMultiplier( int level ){
		if (level == -1){
			return 1;
		} else {
			return 1f / (1f + 0.25f*(level+1));
		}
	}

	public static float healthRegenMultiplier(){
		return healthRegenMultiplier(trinketLevel(SaltCube.class));
	}

	public static float healthRegenMultiplier( int level ){
		switch (level){
			case -1: default:
				return 1;
			case 0:
				return 0.84f;
			case 1:
				return 0.73f;
			case 2:
				return 0.66f;
			case 3:
				return 0.6f;
		}
	}

}
