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
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.ShadowCurse;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.particles.PoisonParticle;
import pd.items.weapon.missiles.arrows.ToxicFruit;
import pd.levels.traps.Trap;
import pd.sprites.ItemSpriteSheet;

public class Sorrowmoss extends Plant {

	{
		image = 2;
		seedClass = Seed.class;
	}
	
	@Override
	public void activate( Char ch ) {
		if (Dungeon.level.heaps.get(pos) != null) Dungeon.level.heaps.get(pos).darkhit();
		if (ch != null) {
			if (ch instanceof Mob){
				Buff.prolong(ch, Trap.HazardAssistTracker.class, Trap.HazardAssistTracker.DURATION);
			}
			Buff.affect(ch, Poison.class).set(4 + Dungeon.legacyDepth() / 2);
			Buff.affect(ch, ShadowCurse.class);
		}
		
		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.center( pos ).burst( PoisonParticle.SPLASH, 3 );
		}
	}
	
	public static class Seed extends Plant.Seed {
		{
			image = ItemSpriteSheet.SPS_SEED_SORROWMOSS;

			plantClass = Sorrowmoss.class;
			explantClass = ExSorrowmoss.class;
		}
	}

	public static class ExSorrowmoss extends SpsFruitBush {
		{ image = 2; harvestCount = 3; harvestClass = ToxicFruit.class; }
	}
}
