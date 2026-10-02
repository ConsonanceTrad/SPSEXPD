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
import pd.actors.mobs.Mob;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Amok extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Amok.class)
			.t("name", "狂乱")
			.t("desc", "狂乱会导致目标陷入极度愤怒和混乱的状态。\n\n狂乱的生物会不分敌我地攻击任何靠近它们的人。\n\n狂乱效果的剩余时长：%s回合");
	}


	{
		type = buffType.NEGATIVE;
		announced = true;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.AMOK;
	}

	@Override
	public void detach() {
		//if our target is an enemy, reset any enemy-to-enemy aggro involving it
		if (target.isAlive()) {
			if (target.alignment == Char.Alignment.ENEMY) {
				for (Mob m : Dungeon.level.mobs()) {
					if (m.alignment == Char.Alignment.ENEMY && m.isTargeting(target)) {
						m.aggro(null);
					}
					if (target instanceof Mob && ((Mob) target).isTargeting(m)){
						((Mob) target).aggro(null);
					}
				}
			}
		}

		super.detach();
	}
}
