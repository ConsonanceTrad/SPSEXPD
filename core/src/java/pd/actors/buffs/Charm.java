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
import pd.actors.Char;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Charm extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Charm.class)
			.t("name", "魅惑")
			.t("heromsg", "你被魅惑了！")
			.t("cant_attack", "你被目标敌人魅惑了，因此无法对其发起攻击。")
			.t("desc", "魅惑是一种能让一对夙敌暂时陷入互相倾慕的控制类魔法。\n\n被魅惑的单位将无法对魅惑施行者进行直接攻击，但依然可以攻击其他敌人。伤痛会激醒被魅惑单位，减少魅惑状态的持续时间。\n\n魅惑效果剩余时间：%s回合");
	}


	public int object = 0;
	public boolean ignoreHeroAllies = false;

	public static final float DURATION = 10f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	private static final String OBJECT          = "object";
	private static final String IGNORE_ALLIES    = "ignore_allies";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( OBJECT, object );
		bundle.put( IGNORE_ALLIES, ignoreHeroAllies );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		object = bundle.getInt( OBJECT );
		ignoreHeroAllies = bundle.getBoolean( IGNORE_ALLIES );
	}

	@Override
	public int icon() {
		return BuffIndicator.HEART;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

	public boolean ignoreNextHit = false;

	public void recover(Object src) {
		if (ignoreHeroAllies && src instanceof Char){
			if (src != Dungeon.hero && ((Char) src).alignment == Char.Alignment.ALLY){
				return;
			}
		}

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
