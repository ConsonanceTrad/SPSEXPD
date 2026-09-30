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

package pd.plants;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Freezing;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FrostImbue;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Mob;
import pd.items.weapon.missiles.arrows.IceFruit;
import pd.levels.traps.Trap;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.ItemSpriteSheet;

public class Icecap extends Plant {
	
	{
		image = 1;
		seedClass = Seed.class;
	}
	
	@Override
	public void activate( Char ch ) {
		
		if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN){
			Buff.affect(ch, FrostImbue.class, FrostImbue.DURATION*0.3f);
		}

		for (int i : PathFinder.NEIGHBOURS9){
			if (!Dungeon.level.solid[pos+i]) {
				Freezing.affect( pos+i );
				if (Actor.findChar(pos+i) instanceof Mob){
					Buff.prolong(Actor.findChar(pos+i), Trap.HazardAssistTracker.class, Trap.HazardAssistTracker.DURATION);
				}
			}
		}
	}
	
	public static class Seed extends Plant.Seed {
		{
			image = ItemSpriteSheet.SPS_SEED_ICECAP;

			plantClass = Icecap.class;
			explantClass = ExIcecap.class;
		}
	}

	public static class ExIcecap extends SpsFruitBush {
		{ image = 1; harvestCount = 3; harvestClass = IceFruit.class; }
	}
}
