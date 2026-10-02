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
import pd.actors.Actor;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorrosiveGas;
import pd.actors.buffs.Buff;
import pd.actors.mobs.Mob;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class CorrosionTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(CorrosionTrap.class)
			.t("name", "腐蚀酸雾陷阱")
			.t("desc", "触发这个陷阱将在附近释放出一片致命的强酸性雾气。");
	}


	{
		color = GREY;
		shape = GRILL;
	}

	@Override
	public void activate() {

		CorrosiveGas corrosiveGas = Blob.seed(pos, 80 + 5 * scalingDepth(), CorrosiveGas.class);
		Sample.INSTANCE.play(Assets.Sounds.GAS);

		corrosiveGas.setStrength(1+scalingDepth()/4);

		for( int i : PathFinder.NEIGHBOURS9) {
			if (Actor.findChar(pos+i) instanceof Mob){
				Buff.prolong(Actor.findChar(pos+i), Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
			}
		}

		GameScene.add(corrosiveGas);

	}
}
