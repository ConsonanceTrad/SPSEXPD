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

public class Haste extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Haste.class)
			.t("name", "极速")
			.t("desc", "强大的能量灌入到你的双腿肌肉上，允许你以不可思议的速度移动！\n\n在极速状态下，你将拥有3倍的移动速度，但是其他行动速度仍然和原来一样。\n\n极速效果剩余时长：%s回合");
	}



	
	{
		type = buffType.POSITIVE;
	}
	
	public static final float DURATION	= 20f;
	
	@Override
	public int icon() {
		return BuffIndicator.HASTE;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1f, 0.8f, 0f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

}
