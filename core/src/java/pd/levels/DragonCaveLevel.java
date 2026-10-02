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
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.consum.eggs.ShadowDragonEgg;
import pd.items.quest.AdventureJournal;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.levels.features.LevelTransition;
import pd.levels.traps.FleecingTrap;
import pd.scenes.GameScene;
import pd.tiles.CustomTilemap;
import pd.tiles.custom.SpsLegacyLevelVisual;
import pd.utils.GLog;
import render.utils.math.Random;

/** Original fixed dragon-cave treasure map and its armor-eating traps. */
public class DragonCaveLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 5 + WIDTH * 37;
	public static final int EGG_POS = 43 + WIDTH * 35;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		map = SpringFestivalLayouts.DRAGON_CAVE.clone();
		if (map.length != length()) return false;
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_PUZZLE, width(), height(), map));
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		return true;
	}

	@Override protected void createMobs() { }

	@Override
	protected void createItems() {
		AdventureJournal journal = Dungeon.hero == null ? null
				: Dungeon.hero.belongings.getItem(AdventureJournal.class);
		boolean firstVisit = journal == null || !journal.isCompleted(17);
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.SOKOBAN_HEAP) {
				drop(firstVisit && Random.Int(5) == 0
						? new ScrollOfUpgrade()
						: new Gold(Random.Int(firstVisit ? 400 : 1, firstVisit ? 800 : 100)), cell)
						.type = Heap.Type.SKELETON;
			}
		}
		if (firstVisit) drop(new ShadowDragonEgg(), EGG_POS);
	}

	@Override
	public void occupyCell(Char ch) {
		super.occupyCell(ch);
		if (ch != Dungeon.hero || map[ch.pos] != Terrain.FLEECING_TRAP) return;
		Hero hero = (Hero) ch;
		if (FleecingTrap.destroyArmor(hero)) {
			GLog.n(pd.messages.Messages.get(this, "armor_destroyed"));
			setSpsTerrain(ch.pos, Terrain.INACTIVE_TRAP);
		} else {
			AdventureJournal journal = hero.belongings.getItem(AdventureJournal.class);
			if (journal != null) journal.execute(hero, AdventureJournal.AC_RETURN);
		}
	}

	private void setSpsTerrain(int cell, int terrain) {
		Level.set(cell, terrain);
		for (CustomTilemap visual : customTiles) {
			if (visual instanceof SpsLegacyLevelVisual) {
				((SpsLegacyLevelVisual) visual).updateTerrainCell(cell, terrain);
			}
		}
		GameScene.updateMap(cell);
	}

	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }
	@Override public String tilesTex() { return Assets.Environment.TILES_PRISON; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_PRISON; }
}
