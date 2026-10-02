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

import pd.Dungeon;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Blindness extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Blindness.class)
			.t("name", "失明")
			.t("heromsg", "你失明了！")
			.t("desc", "失明使周遭的一切陷入黑暗。\n\n失明时，单位的视距被减少至一格，致使远程攻击难堪大用，并使其很容易失去对远处敌人的追踪。此外，失明的英雄无法阅读卷轴或书籍。\n\n失明效果剩余时长：%s回合");
	}




	public static final float DURATION = 10f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	
	@Override
	public void detach() {
		super.detach();
		Dungeon.observe();
	}
	
	@Override
	public int icon() {
		return BuffIndicator.BLINDNESS;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

}
