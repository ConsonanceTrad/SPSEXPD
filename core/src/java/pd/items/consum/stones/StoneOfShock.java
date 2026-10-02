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

package pd.items.consum.stones;

import pd.atlas.items.ConsumScrollAmuletAmuletDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.effects.CellEmitter;
import pd.effects.Lightning;
import pd.effects.particles.EnergyParticle;
import pd.effects.particles.SparkParticle;
import pd.mechanics.pathfind.PathFinder;
import render.noosa.audio.Sample;
import render.utils.data.BArray;

import java.util.ArrayList;
import pd.messages.InlineText;

public class StoneOfShock extends Runestone {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneOfShock.class)
			.t("name", "电击符石")
			.t("desc", "这颗符石被扔出后会爆出一阵电能量，短暂麻痹范围内所有目标并根据电到的目标数量恢复使用者的法杖充能。");
	}



	
	{
		image = ConsumScrollAmuletAmuletDict.STONE_SHOCK_0;
	}
	
	@Override
	protected void activate(int cell) {
		
		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
		
		ArrayList<Lightning.Arc> arcs = new ArrayList<>();
		int hits = 0;
		
		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 2 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				Char n = Actor.findChar(i);
				if (n != null) {
					arcs.add(new Lightning.Arc(cell, n.sprite.center()));
					Buff.prolong(n, Paralysis.class, 1f);
					hits++;
				}
			}
		}
		
		CellEmitter.center( cell ).burst( SparkParticle.FACTORY, 3 );
		
		if (hits > 0) {
			curUser.sprite.parent.addToFront( new Lightning( arcs, null ) );
			curUser.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);
			Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
			
			curUser.belongings.charge(1f + hits);
		}
	
	}
}
