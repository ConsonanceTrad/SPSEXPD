/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.levels.features.LevelTransition;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.utils.math.Random;

/** The original journal page 8 home, randomly using one of its two fixed layouts. */
public class NewRoomLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 23 + WIDTH * 15;
	public static final int LEGACY_EXIT = WIDTH * 47;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_PRISON;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_PRISON;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		Statistics.roomType = Random.Int(2);

		int[] layout;
		String texture;
		if (Statistics.roomType == 0) {
			layout = SaveRoomLayouts.SAFE_ROOM_DEFAULT.clone();
			texture = Assets.Environment.SPS_TILES_PUZZLE;
		} else {
			layout = SaveRoomLayouts.ROOM_OF_FOREST.clone();
			texture = Assets.Environment.SPS_TILES_FOREST;
		}
		if (layout.length != length()) return false;

		map = layout;
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(texture, width(), height(), layout));
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		map[ENTRANCE] = Terrain.ENTRANCE;
		return true;
	}

	@Override
	protected void createMobs() {
	}

	@Override
	protected void createItems() {
	}

	@Override
	public Mob createMob() {
		return null;
	}

	@Override
	public Actor addRespawner() {
		return null;
	}

	@Override
	public int randomRespawnCell(Char ch) {
		return -1;
	}
}
