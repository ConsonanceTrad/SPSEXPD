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

package pd.levels.rooms.standard;

import pd.actors.mobs.Mimic;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.trinkets.MimicTooth;
import pd.levels.GroundItems;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import render.utils.math.Random;

public class SuspiciousChestRoom extends StandardRoom {

	@Override
	public int minWidth() {
		return Math.max(5, super.minWidth());
	}

	@Override
	public int minHeight() {
		return Math.max(5, super.minHeight());
	}

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1 , Terrain.EMPTY );

		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

		Item i = GroundItems.findPrizeItem( level );

		if ( i == null ){
			i = new Gold().random();
		}

		int center = level.pointToCell(center());

		Painter.set(level, center, Terrain.PEDESTAL);

		float mimicChance = 1/3f * MimicTooth.mimicChanceMultiplier();
		if (Random.Float() < mimicChance) {
			level.mobs().add(Mimic.spawnAt(center, i));
		} else {
			level.drop(i, center).type = Heap.Type.CHEST;
		}
	}
}
