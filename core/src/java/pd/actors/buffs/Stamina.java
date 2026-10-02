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

public class Stamina extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Stamina.class)
			.t("name", "体力充沛")
			.t("desc", "你感受到了体内无尽的精力，让你可以以更快的速度移动！\n\n当你处于体力充沛状态下你的移动速度将提升50%%，但是此效果不影响其他行动的耗时。\n\n体力充沛剩余时长：%s回合");
	}




	public static final float DURATION = 100f;
	
	{
		type = buffType.POSITIVE;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.HASTE;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.5f, 1f, 0.5f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}

}
