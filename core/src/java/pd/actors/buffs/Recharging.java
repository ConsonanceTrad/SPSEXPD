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

public class Recharging extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Recharging.class)
			.t("name", "充能")
			.t("desc", "魔力在你体内奔腾而过，提高你的法杖与魔杖的充能速率。\n\n此效果将会在正常充能速率的基础上每回合为你额外回复0.25点法杖充能。\n\n充能效果剩余时长：%s回合");
	}




	public static final float DURATION = 30f;

	{
		type = buffType.POSITIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.RECHARGING;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1, 1, 0);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	//want to process partial turns for this buff, and not count it when it's expiring.
	//firstly, if this buff has half a turn left, should give out half the benefit.
	//secondly, recall that buffs execute in random order, so this can cause a problem where we can't simply check
	//if this buff is still attached, must instead directly check its remaining time, and act accordingly.
	//otherwise this causes inconsistent behaviour where this may detach before, or after, a wand charger acts.
	public float remainder() {
		return Math.min(1f, this.cooldown());
	}
}
