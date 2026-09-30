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

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.particles.FlameParticle;
import pd.scenes.GameScene;
import render.utils.BArray;
import render.noosa.audio.Sample;
import render.utils.PathFinder;

public class BlazingTrap extends Trap {

	{
		color = ORANGE;
		shape = STARS;
	}


	@Override
	public void activate() {
		PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), 2 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				if (Dungeon.level.pit[i] || Dungeon.level.water[i]) {
					GameScene.add(Blob.seed(i, 1, Fire.class));
				} else {
					GameScene.add(Blob.seed(i, 5, Fire.class));
				}
				CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
				if (Actor.findChar(i) instanceof Mob){
					Buff.prolong(Actor.findChar(i), Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
				}
			}
		}
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
	}
}
