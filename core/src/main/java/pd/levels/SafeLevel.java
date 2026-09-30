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
import pd.items.Gold;
import pd.items.Heap;
import pd.items.YellowDewdrop;
import pd.items.food.SmallMeat;
import pd.items.quest.AdventureJournal;
import pd.levels.features.LevelTransition;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.utils.Bundle;
import render.utils.Random;

/** The three original SPS safe-haven layouts, selected by {@link Statistics#roomType}. */
public class SafeLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 23 + WIDTH * 15;
	public static final int LEGACY_EXIT = WIDTH * 47;
	private boolean firstVisit;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	public String tilesTex() {
		// The modern terrain atlas is only a fallback; the original atlas is
		// rendered by SpsLegacyLevelVisual over the complete fixed layout.
		return Assets.Environment.TILES_PRISON;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_PRISON;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		int roomType = Math.max(0, Math.min(2, Statistics.roomType));
		int[] layout;
		String texture;
		switch (roomType) {
			case 0:
				layout = SaveRoomLayouts.ROOM_OF_GRASS.clone();
				texture = Assets.Environment.SPS_TILES_TOWN;
				break;
			case 1:
				layout = SaveRoomLayouts.ROOM_OF_FOREST.clone();
				texture = Assets.Environment.SPS_TILES_FOREST;
				break;
			default:
				layout = SaveRoomLayouts.SAFE_ROOM_DEFAULT.clone();
				texture = Assets.Environment.SPS_TILES_PUZZLE;
				break;
		}
		if (layout.length != length()) return false;

		map = layout;
		firstVisit = !completed();
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(texture, width(), height(), layout));
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		map[ENTRANCE] = Terrain.ENTRANCE;
		return true;
	}

	@Override
	public Mob createMob() {
		return null;
	}

	@Override
	protected void createMobs() {
	}

	@Override
	public Actor addRespawner() {
		return null;
	}

	@Override
	protected void createItems() {
		int goldMin = firstVisit ? 25 : 1;
		int goldMax = firstVisit ? 50 : 10;
		for (int i = 0; i < length(); i++) {
			if (map[i] == Terrain.EMPTY && heaps.get(i) == null && Random.Int(100) > 70) {
				if (Random.Int(5) == 0) {
					drop(new Gold(Random.Int(goldMin, goldMax)), i).type = Heap.Type.CHEST;
				} else if (Random.Int(4) == 0) {
					drop(new SmallMeat(), i).type = Heap.Type.M_WEB;
				} else {
					drop(new YellowDewdrop(), i).type = Heap.Type.E_DUST;
				}
			}
		}
		if (Dungeon.hero != null) {
			AdventureJournal.complete(0);
		}
	}

	private boolean completed() {
		if (Dungeon.hero == null) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		return journal != null && journal.isCompleted(0);
	}

	public boolean firstVisitForTesting() {
		return firstVisit;
	}

	private static final String FIRST_VISIT = "first_visit";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(FIRST_VISIT, firstVisit);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		firstVisit = bundle.getBoolean(FIRST_VISIT);
	}

	@Override
	public int randomRespawnCell(Char ch) {
		return -1;
	}
}
