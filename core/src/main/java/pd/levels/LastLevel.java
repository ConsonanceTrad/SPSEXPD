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
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.items.PuddingCup;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import render.noosa.Group;
import render.noosa.audio.Music;
import render.utils.math.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class LastLevel extends Level {

	{
		color1 = 0x801500;
		color2 = 0xa68521;

		viewDistance = 8;
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.SPS_GAME, true);
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.SPS_TILES_HALLS_LEGACY;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_HALLS;
	}

	@Override
	public void buildFlagMaps() {
		super.buildFlagMaps();
		for (int i=0; i < length(); i++) {
			if (pit[i]) {
				passable[i] = avoid[i] = false;
				solid[i] = true;
			}
		}
	}

	@Override
	public void updateCellFlags(int cell) {
		super.updateCellFlags(cell);
		if (pit[cell]) {
			passable[cell] = avoid[cell] = false;
			solid[cell] = true;
		}
	}

	private static final int WIDTH = 48;
	private static final int HEIGHT = 48;
	private static final int SIZE = 30;
	private static final int ENTRANCE_POS = SIZE * WIDTH + SIZE / 2 + 1;
	private static final int PEDESTAL_POS = (SIZE / 2 + 1) * (WIDTH + 1) - 4 * WIDTH;

	@Override
	protected boolean build() {

		setSize(WIDTH, HEIGHT);
		Arrays.fill( map, Terrain.CHASM );

		Painter.fill(this, 7, 31, 19, 1, Terrain.WALL);
		Painter.fill(this, 15, 10, 3, 21, Terrain.EMPTY);
		Painter.fill(this, 13, 30, 7, 1, Terrain.EMPTY);
		Painter.fill(this, 14, 29, 5, 1, Terrain.EMPTY);
		Painter.fill(this, 14, 9, 5, 7, Terrain.EMPTY);
		Painter.fill(this, 13, 10, 7, 5, Terrain.EMPTY);

		map[ENTRANCE_POS] = Terrain.ENTRANCE;
		transitions.add(new LevelTransition(this, ENTRANCE_POS, LevelTransition.Type.REGULAR_ENTRANCE));

		map[PEDESTAL_POS] = Terrain.PEDESTAL;
		map[PEDESTAL_POS - 1 - width()] = Terrain.STATUE_SP;
		map[PEDESTAL_POS + 1 - width()] = Terrain.STATUE_SP;
		map[PEDESTAL_POS - 1 + width()] = Terrain.STATUE_SP;
		map[PEDESTAL_POS + 1 + width()] = Terrain.STATUE_SP;

		int pos = PEDESTAL_POS;
		map[pos - width()] = map[pos - 1] = map[pos + 1] = map[pos - 2] = map[pos + 2] = Terrain.WATER;
		pos += width();
		map[pos] = map[pos - 2] = map[pos + 2] = map[pos - 3] = map[pos + 3] = Terrain.WATER;
		pos += width();
		map[pos - 3] = map[pos - 2] = map[pos - 1] = map[pos] = map[pos + 1] = map[pos + 2] = map[pos + 3] = Terrain.WATER;
		pos += width();
		map[pos - 2] = map[pos + 2] = Terrain.WATER;

		for (int i = 0; i < length(); i++) {
			if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {
				map[i] = Terrain.EMPTY_DECO;
			}
		}

		feeling = Feeling.NONE;
		viewDistance = 8;

		return true;
	}

	@Override
	public int exit() {
		return PEDESTAL_POS;
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
		drop(new PuddingCup(), PEDESTAL_POS);
	}

	@Override
	public int randomRespawnCell( Char ch ) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int i : PathFinder.NEIGHBOURS8){
			int cell = entrance() + i;
			if (passable[cell]
					&& Actor.findChar(cell) == null
					&& (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])){
				candidates.add(cell);
			}
		}

		if (candidates.isEmpty()){
			return -1;
		} else {
			return Random.element(candidates);
		}
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(HallsLevel.class, "water_name");
			case Terrain.GRASS:
				return Messages.get(HallsLevel.class, "grass_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(HallsLevel.class, "high_grass_name");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(HallsLevel.class, "statue_name");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(HallsLevel.class, "region_deco_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(HallsLevel.class, "water_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(HallsLevel.class, "statue_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(HallsLevel.class, "bookshelf_desc");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(HallsLevel.class, "region_deco_desc");
			default:
				return super.tileDesc( tile );
		}
	}

	@Override
	public Group addVisuals () {
		super.addVisuals();
		HallsLevel.addHallsVisuals(this, visuals);
		return visuals;
	}

}
