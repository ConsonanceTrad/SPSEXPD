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

package pd.levels.rooms.special;

import pd.Dungeon;
import pd.actors.blobs.weather.WeatherOfDead;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.keys.IronKey;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import watabou.utils.Point;
import watabou.utils.Random;

import java.util.ArrayList;

public class CryptRoom extends SpecialRoom {

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );

		Point c = center();
		int cx = c.x;
		int cy = c.y;
		
		Door entrance = entrance();
		
		entrance.set( Door.Type.LOCKED );
		level.addItemToSpawn( new IronKey( Dungeon.depth ) );
		
		if (entrance.x == left) {
			Painter.set( level, new Point( right-1, top+1 ), Terrain.STATUE );
			Painter.set( level, new Point( right-1, bottom-1 ), Terrain.STATUE );
			cx = right - 2;
		} else if (entrance.x == right) {
			Painter.set( level, new Point( left+1, top+1 ), Terrain.STATUE );
			Painter.set( level, new Point( left+1, bottom-1 ), Terrain.STATUE );
			cx = left + 2;
		} else if (entrance.y == top) {
			Painter.set( level, new Point( left+1, bottom-1 ), Terrain.STATUE );
			Painter.set( level, new Point( right-1, bottom-1 ), Terrain.STATUE );
			cy = bottom - 2;
		} else if (entrance.y == bottom) {
			Painter.set( level, new Point( left+1, top+1 ), Terrain.STATUE );
			Painter.set( level, new Point( right-1, top+1 ), Terrain.STATUE );
			cy = top + 2;
		}
		
		level.drop( prize(), cx + cy * level.width() ).type = Heap.Type.TOMB;

		if (Random.Int(10) > 5) {
			WeatherOfDead weather = (WeatherOfDead)level.blobs.get(WeatherOfDead.class);
			if (weather == null) weather = new WeatherOfDead();
			for (int y = top + 1; y < bottom; y++) {
				for (int x = left + 1; x < right; x++) {
					weather.seed(level, x + y * level.width(), 1);
				}
			}
			level.blobs.put(WeatherOfDead.class, weather);
		}

		Heap.Type heapType = Random.Int(2) == 0 ? Heap.Type.CHEST : Heap.Type.HEAP;
		int count = Random.IntRange(2, 3);
		if (heapType == Heap.Type.HEAP) count *= 2;

		ArrayList<Integer> goldCells = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.map[cell] == Terrain.EMPTY && level.heaps.get(cell) == null) goldCells.add(cell);
			}
		}
		for (int i = 0; i < count && !goldCells.isEmpty(); i++) {
			int cell = goldCells.remove(Random.Int(goldCells.size()));
			Item gold = new Gold().random();
			if (i == 0 && heapType == Heap.Type.CHEST) {
				level.drop(gold, cell).type = Heap.Type.MIMIC;
			} else {
				level.drop(gold, cell).type = heapType;
			}
		}
	}
	
	private static Item prize() {
		Item prize = Generator.random(Generator.Category.ARMOR);
		for (int i = 0; i < 3; i++) {
			Item another = Generator.random(Generator.Category.ARMOR);
			if (another.level() > prize.level()) prize = another;
		}
		return prize;
	}
}
