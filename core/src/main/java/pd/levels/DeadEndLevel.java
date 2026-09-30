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

package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.levels.features.LevelTransition;
import pd.tiles.custom.SpsFeatureVisual;
import com.watabou.utils.Random;

import java.util.Arrays;

public class DeadEndLevel extends Level {

	private static final int SIZE = 5;
	
	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}
	
	@Override
	public String tilesTex() {
		return Assets.Environment.SPS_TILES_CAVES_LEGACY;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_HALLS;
	}
	
	@Override
	protected boolean build() {
		
		setSize(48, 48);
		Arrays.fill(map, Terrain.WALL);
		
		for (int i=2; i < SIZE; i++) {
			for (int j=2; j < SIZE; j++) {
				map[i * width() + j] = Terrain.EMPTY;
			}
		}
		
		for (int i=1; i <= SIZE; i++) {
			map[width() + i] =
			map[width() * SIZE + i] =
			map[width() * i + 1] =
			map[width() * i + SIZE] =
				Terrain.WATER;
		}
		
		int entrance = SIZE * width() + SIZE / 2 + 1;
		int sign = (SIZE / 2 + 1) * (width() + 1);
		map[sign] = Terrain.SIGN;
		SpsFeatureVisual signVisual = new SpsFeatureVisual(SpsFeatureVisual.SIGN);
		signVisual.pos(sign, this);
		customTiles.add(signVisual);
		decorate();

		//different exit behaviour depending on main branch or side one
		if (Dungeon.branch == 0) {
			transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
		} else {
			transitions.add(new LevelTransition(this,
					entrance,
					LevelTransition.Type.BRANCH_ENTRANCE,
					Dungeon.depth,
					0,
					LevelTransition.Type.BRANCH_EXIT));
		}
		map[entrance] = Terrain.ENTRANCE;
		
		return true;
	}
	
	@Override
	public Mob createMob() {
		return null;
	}
	
	@Override
	protected void createMobs() {
	}

	public Actor addRespawner() {
		return null;
	}

	@Override
	protected void createItems() {
		// The SPS dead-end floor intentionally contains no loot.
	}

	private void decorate() {
		for (int i = 0; i < length(); i++) {
			if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {
				map[i] = Terrain.EMPTY_DECO;
			} else if (map[i] == Terrain.WALL && Random.Int(8) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}
	}
	
	@Override
	public int randomRespawnCell( Char ch ) {
		return entrance()-width();
	}

}
