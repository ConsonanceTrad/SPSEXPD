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
import pd.sprites.ItemIconSheet;

public class RingOfEvasion extends Ring {

	{
		icon = ItemIconSheet.RING_EVASION;
		buffClass = Evasion.class;
	}

	public String statsInfo() {
		if (isIdentified()){
			return Messages.get(this, "stats", level(), Math.min(6, level() / 5));
		} else {
			return "???";
		}
	}

	public String upgradeStat1(int level){
		return Integer.toString(level);
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Evasion();
	}
	
	public static float evasionMultiplier( Char target ){
		return 1f;
	}

	public static int dexterityBonus(Char target) {
		return Math.min(2, getBonus(target, Evasion.class) / 15);
	}

	public static int stealthBonus(Char target) {
		return Math.min(6, getBonus(target, Evasion.class) / 5);
	}

	public class Evasion extends RingBuff {
		@Override public int level() { return RingOfEvasion.this.level(); }
		@Override public int buffedLvl() { return level(); }
	}
}
