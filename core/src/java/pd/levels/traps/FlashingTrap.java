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
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.mobs.Mob;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class FlashingTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(FlashingTrap.class)
			.t("name", "闪光陷阱")
			.t("desc", "被触发时，这个陷阱将点燃储存在里面的强效闪光粉，使受害者暂时失明，残废，并受到伤害。\n\n这个陷阱的闪光粉储备显然很多，可以多次触发而不损坏。");
	}


	{
		color = GREY;
		shape = ONE_DOT;
	}

	@Override
	public void activate() {
		
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) heap.lighthit();
		
		Char c = Actor.findChar( pos );
		if (c != null) {
			int duration = Random.Int(5, 10) + scalingDepth();
			Buff.prolong(c, Blindness.class, duration);
			Buff.prolong(c, Cripple.class, duration);
			
			if (c instanceof Mob) {
				if (((Mob)c).state == ((Mob)c).HUNTING) ((Mob)c).state = ((Mob)c).WANDERING;
				((Mob)c).beckon( Dungeon.level.randomDestination( c ) );
			}
		}
		
		if (Dungeon.level.heroFOV[pos]) {
			GameScene.flash(0xFFFFFF);
			CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
			Sample.INSTANCE.play( Assets.Sounds.BLAST );
		}
		
	}

}
