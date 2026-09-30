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
import pd.actors.mobs.npcs.TownNpc;
import pd.items.quest.AdventureJournal;
import pd.levels.features.LevelTransition;
import pd.tiles.custom.SpsLegacyLevelVisual;

/** Original town-layout refuge reached through the Shadow Eater key. */
public class ShadowEaterLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 25 + WIDTH * 21;
	public static final int LEGACY_EXIT = 5 + WIDTH * 40;
	public static final int PAINTER_POS = 16 + WIDTH * 21;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		map = TownLayouts.TOWN_LAYOUT.clone();
		if (map.length != length()) return false;
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_TOWN, width(), height(), map));
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		return true;
	}

	@Override protected void createMobs() { }

	@Override
	protected void createItems() {
		TownNpc painter = new TownNpc().configure(TownNpc.Spec.NUT_PAINTER);
		painter.pos = PAINTER_POS;
		mobs().add(painter);
		if (Dungeon.hero != null) AdventureJournal.complete(16);
	}

	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }
	@Override public String tilesTex() { return Assets.Environment.TILES_CITY; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_PRISON; }
}
