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
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Terror extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Terror.class)
			.t("name", "恐惧")
			.t("desc", "恐惧是使敌人陷入不可控制的恐慌的操纵性魔法。\n\n恐惧中的角色会远离自己的敌人，试图和敌人隔开尽可能多的门或墙。然而疼痛的刺激会减少恐惧的持续时间。\n\n恐惧效果剩余时长：%s");
	}




	public int object = 0;

	private static final String OBJECT    = "object";

	public static final float DURATION = 20f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(OBJECT, object);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		object = bundle.getInt( OBJECT );
	}

	@Override
	public int icon() {
		return BuffIndicator.TERROR;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	public boolean ignoreNextHit = false;

	public void recover() {
		if (ignoreNextHit){
			ignoreNextHit = false;
			return;
		}
		spend(-5f);
		if (cooldown() <= 0){
			detach();
		}
	}
}
