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
import pd.actors.blobs.Foliage;
import pd.items.Honeypot;
import pd.items.Item;
import pd.items.eggs.EasterEgg;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import pd.plants.BlandfruitBush;
import pd.plants.Plant;
import pd.plants.Seedpod;
import watabou.utils.Random;

import java.util.ArrayList;
import java.util.Calendar;

public class GardenRoom extends SpecialRoom {

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.HIGH_GRASS );
		Painter.fill( level, this, 2, Terrain.WATER );
		
		entrance().set( Door.Type.REGULAR );

		int bushes = Random.Int(3);
		if (bushes == 0) {
			plant(level, new Seedpod.Seed());
		} else if (bushes == 1) {
			plant(level, new BlandfruitBush.Seed());
		} else if (Random.Int(5) == 0) {
			plant(level, new Seedpod.Seed());
			plant(level, new BlandfruitBush.Seed());
		}

		if (Dungeon.depth < 25) drop(level, new Honeypot());
		if (Random.Int(50) == 0 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL) {
			drop(level, new EasterEgg());
		}
		
		Foliage light = (Foliage)level.blobs.get( Foliage.class );
		if (light == null) {
			light = new Foliage();
		}
		for (int i=top + 1; i < bottom; i++) {
			for (int j=left + 1; j < right; j++) {
				light.seed( level, j + level.width() * i, 1 );
			}
		}
		level.blobs.put( Foliage.class, light );
	}

	private void plant(Level level, Plant.Seed seed) {
		ArrayList<Integer> candidates = cellsWithoutPlant(level);
		if (!candidates.isEmpty()) level.plant(seed, Random.element(candidates));
	}

	private void drop(Level level, Item item) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.heaps.get(cell) == null) candidates.add(cell);
			}
		}
		if (!candidates.isEmpty()) level.drop(item, Random.element(candidates));
	}

	private ArrayList<Integer> cellsWithoutPlant(Level level) {
		ArrayList<Integer> result = new ArrayList<>();
		for (int y = top + 1; y < bottom; y++) {
			for (int x = left + 1; x < right; x++) {
				int cell = x + y * level.width();
				if (level.plants.get(cell) == null) result.add(cell);
			}
		}
		return result;
	}
}
