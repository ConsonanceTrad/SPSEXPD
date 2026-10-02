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
import pd.actors.blobs.Electricity;
import pd.actors.buffs.Buff;
import pd.actors.mobs.Mob;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;
import render.utils.data.BArray;
import pd.messages.InlineText;

public class StormTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(StormTrap.class)
			.t("name", "雷暴陷阱")
			.t("desc", "一种储存着庞大电能的机关。触发这个陷阱会让它把能量释放出来形成大范围的雷电风暴。");
	}



	
	{
		color = YELLOW;
		shape = STARS;
	}
	
	@Override
	public void activate() {
		
		if (Dungeon.level.heroFOV[pos]){
			Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
		}
		
		PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), 2 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				GameScene.add(Blob.seed(i, 20, Electricity.class));
				if (Actor.findChar(i) instanceof Mob){
					Buff.prolong(Actor.findChar(i), Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
				}
			}
		}
	}
	
}
