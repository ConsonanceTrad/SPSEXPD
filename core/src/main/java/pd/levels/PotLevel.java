/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.items.quest.AdventureJournal;
import pd.levels.features.LevelTransition;
import pd.tiles.custom.SpsLegacyLevelVisual;

/** The fixed honey-themed room reached with the legacy pot key. */
public class PotLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 23 + WIDTH * 15;
	public static final int LEGACY_EXIT = WIDTH * 47;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		map = SaveRoomLayouts.SAFE_ROOM_DEFAULT.clone();
		if (map.length != length()) return false;
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_HONEY, width(), height(), map));
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		return true;
	}

	@Override protected void createMobs() { }
	@Override protected void createItems() {
		if (Dungeon.hero != null) AdventureJournal.complete(15);
	}
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }
	@Override public String tilesTex() { return Assets.Environment.TILES_PRISON; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_HONEY; }
}
