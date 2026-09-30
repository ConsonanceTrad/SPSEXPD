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

package pd.items.rings;

import pd.actors.Char;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;

public class RingOfAccuracy extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_ACCURACY;
		buffClass = Accuracy.class;
	}
	
	public String statsInfo() {
		if (isIdentified()){
			return Messages.get(this, "stats", level(), Math.min(3, level() / 10));
		} else {
			return "???";
		}
	}

	public String upgradeStat1(int level){
		return Integer.toString(level);
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Accuracy();
	}
	
	public static float accuracyMultiplier( Char target ){
		return (float)Math.pow(0.75, -getBonus(target, Accuracy.class));
	}

	public static int reachBonus(Char target) {
		int bonus = 0;
		for (Accuracy buff : target.buffs(Accuracy.class)) bonus += Math.min(buff.level(), 30);
		return bonus / 10;
	}
	
	public class Accuracy extends RingBuff {
		@Override public int level() { return RingOfAccuracy.this.level(); }
		@Override public int buffedLvl() { return level(); }
	}
}
