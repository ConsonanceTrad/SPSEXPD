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

package pd.actors.buffs;

import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Fury extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Fury.class)
			.t("name", "愤怒")
			.t("heromsg", "你陷入了暴怒之中！")
			.t("desc", "你非常暴怒，很明显敌人并不喜欢这样的你。\n\n一股猛烈的怒火在你体内燃烧，增加你50%%的物理攻击伤害。\n\n只要你的生命还低于上限的50%%，该效果就会存在。");
	}



	
	public static float LEVEL	= 0.5f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}
	
	@Override
	public boolean act() {
		if (target.HP > target.HT * LEVEL) {
			detach();
		}
		
		spend( TICK );
		
		return true;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.FURY;
	}
}
