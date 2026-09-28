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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class LibraryRoom extends SpecialRoom {

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );
		
		Door entrance = entrance();

		Painter.fill( level, left + 1, top + 1, width() - 2, 1, Terrain.BOOKSHELF );
		Point pot;
		if (entrance.x == left) {
			pot = new Point(right - 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
			Painter.set(level, entrance.x + 1, entrance.y, Terrain.EMPTY_SP);
		} else if (entrance.x == right) {
			pot = new Point(left + 1, Random.Int(2) == 0 ? top + 1 : bottom - 1);
			Painter.set(level, entrance.x - 1, entrance.y, Terrain.EMPTY_SP);
		} else if (entrance.y == top) {
			pot = new Point(Random.Int(2) == 0 ? left + 1 : right - 1, bottom - 1);
			Painter.set(level, entrance.x, entrance.y + 1, Terrain.EMPTY_SP);
		} else {
			pot = new Point(Random.Int(2) == 0 ? left + 1 : right - 1, top + 1);
			Painter.set(level, entrance.x, entrance.y - 1, Terrain.EMPTY_SP);
		}

		Painter.set(level, pot, Terrain.ALCHEMY);
		Alchemy alchemy = new Alchemy();
		alchemy.seed(level, pot.x + level.width() * pot.y, 1);
		level.blobs.put(Alchemy.class, alchemy);

		ArrayList<Integer> rewardCells = rewardCells(level);
		int n = Random.IntRange(2, 3);
		for (int i = 0; i < n; i++) level.drop(scrollPrize(level), Random.element(rewardCells));
		for (int i = 0; i < n; i++) level.drop(potionPrize(level), Random.element(rewardCells));

		entrance.set( Door.Type.LOCKED );
		level.addItemToSpawn( new IronKey( Dungeon.depth ) );
	}

	private ArrayList<Integer> rewardCells(Level level) {
		ArrayList<Integer> result = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.map[cell] != Terrain.BOOKSHELF) result.add(cell);
			}
		}
		return result;
	}

	private static Item scrollPrize(Level level) {
		Item prize = level.findPrizeItem(Scroll.class);
		return prize == null ? Generator.random(Generator.Category.SCROLL) : prize;
	}

	private static Item potionPrize(Level level) {
		Item prize = level.findPrizeItem(Scroll.class);
		return prize == null ? Generator.random(Generator.Category.POTION) : prize;
	}
}
