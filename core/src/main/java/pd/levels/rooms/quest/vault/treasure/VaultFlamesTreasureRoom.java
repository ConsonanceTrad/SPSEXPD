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

package pd.levels.rooms.quest.vault.treasure;

import pd.items.Heap;
import pd.items.Item;
import pd.items.potions.PotionOfPurity;
import pd.items.quest.DwarfToken;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.VaultLevel;
import pd.levels.painters.Painter;
import render.utils.Point;
import render.utils.Random;

public class VaultFlamesTreasureRoom extends VaultTreasureRoom {

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );

		Painter.fillEllipse( level, this, 2, Terrain.EMPTY );

		Point c = center();

		int[] treasurePositions = new int[]{
				level.pointToCell(c)-3*level.width(),
				level.pointToCell(c)+3,
				level.pointToCell(c)+3*level.width(),
				level.pointToCell(c)-3,
				level.pointToCell(c)-3*level.width(),
				level.pointToCell(c)+3,
		};

		int treasureIdx;
		int consumablePos;
		if (entrance().x == left){
			treasureIdx = 1;
		} else if (entrance().y == top){
			treasureIdx = 2;
		} else if (entrance().x == right){
			treasureIdx = 3;
		} else {
			treasureIdx = 4;
		}
		Painter.set(level, treasurePositions[treasureIdx-1], Terrain.PEDESTAL);
		Painter.set(level, treasurePositions[treasureIdx], Terrain.PEDESTAL);
		Painter.set(level, treasurePositions[treasureIdx+1], Terrain.PEDESTAL);

		Item treasureItem = ((VaultLevel)level).createEquipment(2);
		level.drop(treasureItem, treasurePositions[treasureIdx]).type = Heap.Type.CHEST;

		for (int x = left + 2; x <= right - 2; x++){
			for (int y = top + 2; y <= bottom - 2; y++){
				int cell = x + level.width()*y;
				if (level.map[cell] == Terrain.EMPTY) {
					VaultLevel.VaultFlameTrap.setupTrap(level, x + level.width() * y, 1, 1, 1);
				}
			}
		}

		if (Math.abs(c.x - entrance().x) <= 1 || Math.abs(c.y - entrance().y) <= 1){
			Painter.drawInside(level, this, entrance(), 1, Terrain.EMPTY);
		} else {
			Painter.drawInside(level, this, entrance(), 2, Terrain.EMPTY);
		}

		treasureItem = ((VaultLevel) level).findT3SolveItem();
		if (treasureItem == null) {
			treasureItem = ((VaultLevel) level).createConsumabe(2);
		}
		if (Random.Int(2) == 0){
			level.drop(treasureItem, treasurePositions[treasureIdx-1]);
			level.drop(new DwarfToken(), treasurePositions[treasureIdx+1]);
		} else {
			level.drop(new DwarfToken(), treasurePositions[treasureIdx-1]);
			level.drop(treasureItem, treasurePositions[treasureIdx+1]);
		}

		level.addItemToSpawn(new PotionOfPurity());

		entrance().set(Door.Type.REGULAR);

	}

	@Override
	public boolean canPlaceGrass(Point p) {
		return false; //no grass to obstruct vision
	}

	@Override
	public boolean canConnect(Point p) {
		Point c = center();
		return (Math.abs(c.x - p.x) <= 2 || Math.abs(c.y - p.y) <= 2) && super.canConnect(p);
	}

}
