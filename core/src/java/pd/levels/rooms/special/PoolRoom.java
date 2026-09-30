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

import pd.actors.mobs.Piranha;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.potions.PotionOfInvisibility;
import pd.levels.GroundItems;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import render.utils.math.Random;

import java.util.ArrayList;

public class PoolRoom extends SpecialRoom {

	private static final int NPIRANHAS = 4;
	
	@Override
	public int minWidth() {
		return 6;
	}
	
	@Override
	public int minHeight() {
		return 6;
	}
	
	public void paint(Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.WATER );
		
		Door door = entrance();
		door.set( Door.Type.REGULAR );

		int x = -1;
		int y = -1;
		if (door.x == left) {
			
			x = right - 1;
			y = (top + bottom) / 2;
			
		} else if (door.x == right) {
			
			x = left + 1;
			y = (top + bottom) / 2;
			
		} else if (door.y == top) {
			
			x = (left + right) / 2;
			y = bottom - 1;
			
		} else if (door.y == bottom) {
			
			x = (left + right) / 2;
			y = top + 1;
			
		}
		
		int pos = x + y * level.width();
		level.drop( prize( level ), pos ).type = Random.Int(3) == 0 ? Heap.Type.CHEST : Heap.Type.HEAP;
		Painter.set( level, pos, Terrain.PEDESTAL );
		
		GroundItems.addItemToSpawn( level,  new PotionOfInvisibility() );

		ArrayList<Integer> fishCells = new ArrayList<>();
		for (int fy = top + 1; fy < bottom; fy++) {
			for (int fx = left + 1; fx < right; fx++) {
				int cell = fx + fy * level.width();
				if (level.map[cell] == Terrain.WATER && level.mobs().findMob(cell) == null) fishCells.add(cell);
			}
		}
		for (int i = 0; i < NPIRANHAS && !fishCells.isEmpty(); i++) {
			Piranha piranha = new Piranha();
			piranha.pos = fishCells.remove(Random.Int(fishCells.size()));
			level.mobs().add( piranha );
		}
	}
	
	private static Item prize( Level level ) {

		Item prize;

		if (Random.Int(3) != 0){
			prize = GroundItems.findPrizeItem( level );
			if (prize != null)
				return prize;
		}

		prize = Generator.random(Random.oneOf(Generator.Category.MELEEWEAPON, Generator.Category.ARMOR));
		for (int i = 0; i < 4; i++) {
			Item another = Generator.random(Random.oneOf(Generator.Category.MELEEWEAPON, Generator.Category.ARMOR));
			if (another.level() > prize.level()) prize = another;
		}

		return prize;
	}
}
