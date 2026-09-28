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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StorageRoom extends SpecialRoom {

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

		Door entrance = entrance();
		if (entrance.x == left) {
			Painter.fill(level, right - 1, top + 1, 1, height() - 2, Terrain.EMPTY);
			Painter.fill(level, right - 2, top + 1, 1, height() - 2, Terrain.BOOKSHELF);
		} else if (entrance.x == right) {
			Painter.fill(level, left + 1, top + 1, 1, height() - 2, Terrain.EMPTY);
			Painter.fill(level, left + 2, top + 1, 1, height() - 2, Terrain.BOOKSHELF);
		} else if (entrance.y == top) {
			Painter.fill(level, left + 1, bottom - 1, width() - 2, 1, Terrain.EMPTY);
			Painter.fill(level, left + 1, bottom - 2, width() - 2, 1, Terrain.BOOKSHELF);
		} else {
			Painter.fill(level, left + 1, top + 1, width() - 2, 1, Terrain.EMPTY);
			Painter.fill(level, left + 1, top + 2, width() - 2, 1, Terrain.BOOKSHELF);
		}

		ArrayList<Integer> skeletonCells = cells(level, Terrain.EMPTY, true);
		int skeletons = Math.min(2 + Random.Int(3), skeletonCells.size());
		for (int i = 0; i < skeletons; i++) {
			int cell = skeletonCells.remove(Random.Int(skeletonCells.size()));
			level.drop(prize(), cell).type = Heap.Type.SKELETON;
		}

		ArrayList<Integer> chestCells = cells(level, Terrain.EMPTY_SP, true);
		if (!chestCells.isEmpty()) {
			level.drop(prize(), Random.element(chestCells)).type = Heap.Type.CHEST;
		}

		entrance.set( Door.Type.REGULAR );
		level.addItemToSpawn( new PotionOfLiquidFlame() );
	}

	private ArrayList<Integer> cells(Level level, int terrain, boolean requireNoHeap) {
		ArrayList<Integer> result = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.map[cell] == terrain && (!requireNoHeap || level.heaps.get(cell) == null)) result.add(cell);
			}
		}
		return result;
	}

	private static Item prize() {

		Item prize = Generator.random();
		if (prize != null) return prize;
		
		return Generator.random( Random.oneOf(
			Generator.Category.POTION,
			Generator.Category.SCROLL,
			Generator.Category.GOLD,
			Generator.Category.WAND
		) );
	}
}
