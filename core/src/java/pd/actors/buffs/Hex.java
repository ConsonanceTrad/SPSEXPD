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

public class Hex extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Hex.class)
			.t("name", "幻惑")
			.t("heromsg", "你的精神受到了魔法的干扰！")
			.t("desc", "干扰集中力的黑暗魔法，使目标无法准确地判断方位。\n\n幻惑状态会降低目标20%%的精准与闪避属性，使其在战斗中更容易处于下风。\n\n幻惑效果剩余时长：%s回合");
	}



	
	public static final float DURATION	= 30f;
	
	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.HEX;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
}
