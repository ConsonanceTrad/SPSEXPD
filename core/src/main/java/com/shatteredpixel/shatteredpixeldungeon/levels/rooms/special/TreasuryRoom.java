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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TreasuryRoom extends SpecialRoom {

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );
		Painter.fill( level, this, 2, Terrain.EMPTY );

		int center = level.pointToCell(center());
		Item first = prizeUncursed();
		Item second = prizeUncursed();
		for (int attempt = 0; attempt < 50 && first.getClass() == second.getClass(); attempt++) {
			second = prizeUncursed();
		}

		level.drop(first, center).type = Heap.Type.CRYSTAL_CHEST;
		ArrayList<Integer> neighbourCells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (level.insideMap(cell) && level.map[cell] == Terrain.EMPTY && level.heaps.get(cell) == null) {
				neighbourCells.add(cell);
			}
		}
		if (!neighbourCells.isEmpty()) {
			level.drop(second, Random.element(neighbourCells)).type = Heap.Type.CRYSTAL_CHEST;
		}
		level.addItemToSpawn(new GoldenKey(Dungeon.depth));

		ArrayList<Integer> emptyCells = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.map[cell] == Terrain.EMPTY && level.heaps.get(cell) == null) emptyCells.add(cell);
			}
		}
		if (!emptyCells.isEmpty()) {
			Item third = prizeUncursed();
			for (int attempt = 0; attempt < 50 && first.getClass() == second.getClass()
					&& first.getClass() == third.getClass(); attempt++) {
				third = prizeUncursed();
			}
			level.drop(third, Random.element(emptyCells)).type = Heap.Type.CRYSTAL_CHEST;
		}
		
		entrance().set( Door.Type.LOCKED );
		level.addItemToSpawn( new IronKey( Dungeon.depth ) );
	}

	private static Item prizeUncursed() {
		Item item = Generator.random(Random.oneOf(
				Generator.Category.WAND, Generator.Category.RING, Generator.Category.ARTIFACT));
		if (item.cursed && item.isUpgradable()) {
			item.cursed = false;
			if (item.level() < 0) item.upgrade(-item.level());
		}
		return item;
	}
}
