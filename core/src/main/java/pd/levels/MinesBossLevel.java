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
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.LitTower;
import pd.actors.mobs.MineSentinel;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Otiluke;
import pd.items.Heap;
import pd.items.Palantir;
import pd.items.keys.IronKey;
import pd.levels.features.LevelTransition;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.tiles.custom.SpsLegacyLevelVisual;
import pd.windows.WndMessage;
import render.utils.Bundle;
import render.utils.Random;

/** The original SPS energy-core arena, journal destination 7. */
public class MinesBossLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 17 + WIDTH * 44;
	public static final int LEGACY_EXIT = 0;
	public static final int BOSS_CELL = 33 + WIDTH * 10;
	public static final int KEY_CELL = 30 + WIDTH * 44;
	public static final int PALANTIR_CELL = 14 + WIDTH * 10;

	private boolean entered;

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
		map = MineBossLayouts.MINE_BOSS.clone();
		if (map.length != length()) return false;
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.1f) map[cell] = Terrain.WATER;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.1f) map[cell] = Terrain.OLD_HIGH_GRASS;
		}
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_PUZZLE, width(), height(), map));
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		map[ENTRANCE] = Terrain.ENTRANCE;
		return true;
	}

	@Override
	protected void createMobs() {
		for (int cell = 0; cell < length(); cell++) {
			Mob mob = null;
			if (MineBossLayouts.MINE_BOSS[cell] == Terrain.SOKOBAN_SHEEP) mob = new MineSentinel();
			else if (MineBossLayouts.MINE_BOSS[cell] == Terrain.CORNER_SOKOBAN_SHEEP) mob = new LitTower();
			if (mob != null) {
				mob.pos = cell;
				mobs.add(mob);
			}
		}
		Otiluke boss = new Otiluke();
		boss.pos = BOSS_CELL;
		mobs.add(boss);
	}

	@Override
	protected void createItems() {
		drop(new IronKey(Dungeon.depth), KEY_CELL).type = Heap.Type.CHEST;
		drop(new Palantir(), PALANTIR_CELL);
	}

	@Override
	public void occupyCell(Char ch) {
		if (ch instanceof Hero && !entered) {
			entered = true;
			locked = true;
			GameScene.show(new WndMessage(Messages.get(this, "intro")));
		}
		super.occupyCell(ch);
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
		for (int tries = 0; tries < 200; tries++) {
			int cell = Random.Int(length());
			if (passable[cell] && Actor.findChar(cell) == null) return cell;
		}
		return -1;
	}

	private static final String ENTERED = "entered";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ENTERED, entered);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		entered = bundle.getBoolean(ENTERED);
	}
}
