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

public class Adrenaline extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Adrenaline.class)
			.t("name", "激素涌动")
			.t("desc", "由肾上腺素带来的纯粹的潜能爆发，激素涌动能够增强一名角色的移动和攻击速度。\n\n激素涌动允许角色拥有2倍的移动速度和1.5倍的攻击速度。\n\n激素涌动持续时间：%s回合");
	}



	
	{
		type = buffType.POSITIVE;
		
		announced = true;
	}
	
	public static final float DURATION	= 10f;
	
	@Override
	public int icon() {
		return BuffIndicator.UPGRADE;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1, 0, 0);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
	
}
