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
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class BurningTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(BurningTrap.class)
			.t("name", "烈焰陷阱")
			.t("desc", "踩进这个陷阱会点燃某种化学混合物，导致附近一块区域起火。");
	}




	{
		color = ORANGE;
		shape = DOTS;
	}

	@Override
	public void activate() {
		
		for( int i : PathFinder.NEIGHBOURS9) {
			if (!Dungeon.level.solid[pos + i]) {
				GameScene.add( Blob.seed( pos+i, 2, Fire.class ) );
				CellEmitter.get( pos+i ).burst( FlameParticle.FACTORY, 5 );
				if (Actor.findChar(pos+i) instanceof Mob){
					Buff.prolong(Actor.findChar(pos+i), Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
				}
			}
		}
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
	}
}
