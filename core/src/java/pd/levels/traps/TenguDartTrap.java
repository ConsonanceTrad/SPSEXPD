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

package pd.levels.traps;

import pd.Challenges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.mobs.Tengu;
import pd.messages.InlineText;

public class TenguDartTrap extends PoisonDartTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(TenguDartTrap.class)
			.t("desc", "显然，天狗做了充分的战斗准备。这个陷阱会激活一个隐藏的飞镖发射器，向距离最近且不是天狗的单位发射一枚毒镖。\n\n陷阱的制造技巧极其高深，不使用魔法手段的话，触发装置完全无法找到。不过陷阱在刚被布置的短时间内是可以用肉眼观察到的。");
	}

	
	{
		canBeHidden = true;
		canBeSearched = false;
	}
	
	@Override
	protected int poisonAmount() {
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			return 15; //50 damage total, equal to poison dart traps on floor 10
		} else {
			return 8; //17 damage total
		}
	}
	
	@Override
	protected boolean canTarget(Char ch) {
		return !(ch instanceof Tengu);
	}
}
