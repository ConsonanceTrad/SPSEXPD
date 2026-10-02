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
import render.noosa.Image;
import pd.messages.InlineText;

public class Slow extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Slow.class)
			.t("name", "迟缓")
			.t("desc", "减速魔法影响了目标的时间流速，在目标眼中所有的事物都移动得飞快。\n\n迟缓下的角色执行所有行动花费的时间是正常状态下的两倍。\n\n迟缓效果剩余时长：%s回合");
	}


	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	public static final float DURATION = 10f;

	@Override
	public int icon() {
		return BuffIndicator.TIME;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1f, 0.33f, 0.2f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

}
