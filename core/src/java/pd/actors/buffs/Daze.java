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

public class Daze extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Daze.class)
			.t("name", "恍惚")
			.t("heromsg", "你感到头晕目眩！")
			.t("desc", "耳畔嗡鸣，视野模糊，战斗因此变得十分困难。\n\n在恍惚状态下，精准和闪避都会减少50%%，使得命中和躲避都变得更加困难。\n\n剩余回合数：%s");
	}




	public static final float DURATION	= 5f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.DAZE;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

}
