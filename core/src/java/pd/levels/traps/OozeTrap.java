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

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.actors.mobs.Mob;
import pd.effects.Splash;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;

public class OozeTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(OozeTrap.class)
			.t("name", "淤泥陷阱")
			.t("desc", "这个陷阱将会洒出腐蚀性的淤泥，它将烧灼你的皮肤直到被洗掉。");
	}




	{
		color = GREEN;
		shape = DOTS;
	}

	@Override
	public void activate() {

		for( int i : PathFinder.NEIGHBOURS9) {
			if (!Dungeon.level.solid[pos + i]) {
				Splash.at( pos + i, 0x000000, 5);
				Char ch = Actor.findChar( pos + i );
				if (ch != null && !ch.flying){
					Buff.affect(ch, Ooze.class).set( Ooze.DURATION );
					if (ch instanceof Mob){
						Buff.prolong(ch, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
					}
				}
			}
		}
	}
}
