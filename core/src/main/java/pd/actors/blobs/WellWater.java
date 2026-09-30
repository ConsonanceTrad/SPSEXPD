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

package pd.actors.blobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.journal.Notes;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import render.utils.PathFinder;
import render.utils.Random;

public abstract class WellWater extends Blob {

	{
		alwaysVisible = true;
	}

	@Override
	protected void evolve() {
		int cell;
		boolean seen = false;
		for (int i=area.top-1; i <= area.bottom; i++) {
			for (int j = area.left-1; j <= area.right; j++) {
				cell = j + i* Dungeon.level.width();
				if (Dungeon.level.insideMap(cell)) {
					off[cell] = cur[cell];
					volume += off[cell];
				}
			}
		}
	}
	
	protected boolean affect( int pos ) {
		
		Heap heap;
		
		if (Dungeon.hero != null && pos == Dungeon.hero.pos && affectHero( Dungeon.hero )) {
			
			clear(pos);
			return true;
			
		} else if ((heap = Dungeon.level.heaps.get( pos )) != null) {
			
			Item oldItem = heap.peek();
			if (oldItem == null) return false;
			Item newItem = affectItem( oldItem, pos );
			
			if (newItem != null) {
				
				if (newItem == oldItem) {

				} else if (oldItem.quantity() > 1) {

					oldItem.quantity( oldItem.quantity() - 1 );
					heap.drop( newItem );
					
				} else {
					heap.replace( oldItem, newItem );
				}
				
				if (heap.sprite != null) heap.sprite.link();
				clear(pos);
				
				return true;
				
			} else {
				
				int[] candidates = new int[PathFinder.NEIGHBOURS8.length];
				int count = 0;
				for (int offset : PathFinder.NEIGHBOURS8) {
					int candidate = pos + offset;
					if (Dungeon.level.insideMap(candidate)
							&& (Dungeon.level.passable[candidate] || Dungeon.level.avoid[candidate])
							&& Actor.findChar(candidate) == null) {
						candidates[count++] = candidate;
					}
				}
				if (count == 0) return false;

				Item moved = heap.pickUp();
				if (moved == null) return false;
				Heap destination = Dungeon.level.drop(moved, candidates[Random.Int(count)]);
				if (destination != null && destination.sprite != null) destination.sprite.drop(pos);
				
				return false;
				
			}
			
		} else {
			
			return false;
			
		}
	}
	
	protected abstract boolean affectHero( Hero hero );
	
	protected abstract Item affectItem( Item item, int pos );
	
	public static void affectCell( int cell ) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;
		
		Class<?>[] waters = {WaterOfHealth.class, WaterOfAwareness.class, WaterOfTransmutation.class};
		
		for (Class<?>waterClass : waters) {
			WellWater water = (WellWater)Dungeon.level.blobs.get( waterClass );
			if (water != null &&
				water.volume > 0 &&
				water.cur[cell] > 0 &&
				water.affect( cell )) {
				
				Level.set( cell, Terrain.EMPTY_WELL );
				GameScene.updateMap( cell );

				if (water.landmark() != null) {
					if (water.volume <= 0) {
						Notes.remove(water.landmark());
					} else {
						boolean removing = true;
						for (int i = 0; i < water.cur.length; i++){
							if (water.cur[i] > 0 && Dungeon.level.visited[i]){
								removing = false;
								break;
							}
						}
						if (removing) Notes.remove(water.landmark());
					}
				}
				
				return;
			}
		}
	}
}
