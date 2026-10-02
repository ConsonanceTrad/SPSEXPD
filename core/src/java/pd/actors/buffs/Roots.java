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

import pd.actors.Char;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Roots extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Roots.class)
			.t("name", "缠绕")
			.t("heromsg", "你不能移动了！")
			.t("desc", "一些根系(不论是自然或魔法产生)缠到了脚上，牢牢将其缚在地面。\n\n根系会让目标被困在当前区域，使其不能移动或使用大部分移动技能。其他行动不受干扰，包括传送效果。\n\n缠绕效果剩余时长：%s回合");
	}


	public static final float DURATION = 5f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	
	@Override
	public boolean attachTo( Char target ) {
		if (!target.flying && super.attachTo( target )) {
			target.rooted = true;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		target.rooted = false;
		super.detach();
	}
	
	@Override
	public int icon() {
		return BuffIndicator.ROOTS;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
}
