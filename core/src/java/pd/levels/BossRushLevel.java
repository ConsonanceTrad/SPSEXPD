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
import pd.actors.mobs.BossRushBoss;
import pd.actors.mobs.Dragonking;
import pd.actors.mobs.Mob;
import pd.actors.mobs.UAmulet;
import pd.actors.mobs.UDM300;
import pd.actors.mobs.UGoo;
import pd.actors.mobs.UIcecorps2;
import pd.actors.mobs.UIcecorps;
import pd.actors.mobs.UKing;
import pd.actors.mobs.UTengu;
import pd.actors.mobs.UYog;
import pd.items.BossRush;
import pd.items.quest.AdventureJournal;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.Arrays;

/** The original randomized five-lane magic-cave boss-rush arena. */
public class BossRushLevel extends Level {
	public static final Class<?>[] BOSS_SEQUENCE = {
			Dragonking.class, UGoo.class, UTengu.class, UDM300.class, UKing.class,
			UIcecorps.class, UIcecorps2.class, UYog.class, UAmulet.class
	};

	public static final int WIDTH = 24;
	public static final int HEIGHT = 24;
	public static final int ENTRANCE = 12 + 12 * WIDTH;
	private static final int ROOM_LEFT = WIDTH / 2 - 1;
	private static final int ROOM_RIGHT = WIDTH / 2 + 1;
	private static final int ROOM_TOP = HEIGHT / 2 - 1;
	private static final int ROOM_BOTTOM = HEIGHT / 2 + 1;

	private int bossStage;
	private int arenaExit = -1;
	private boolean completed;

	{
		color1 = 0x801500;
		color2 = 0xa68521;
		viewDistance = 8;
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_HALLS;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_CITY;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		Arrays.fill(map, Terrain.WALL);

		for (int lane = 0; lane < 5; lane++) {
			int top = Random.IntRange(2, ROOM_TOP - 1);
			int bottom = Random.IntRange(ROOM_BOTTOM + 1, 22);
			Painter.fill(this, 2 + lane * 4, top, 4, bottom - top + 1, Terrain.EMPTY);
			if (lane == 2) arenaExit = lane * 4 + 3 + (top - 1) * width();
			for (int dx = 0; dx < 4; dx++) {
				if (Random.Int(2) == 0) {
					int y = Random.IntRange(top + 1, bottom - 1);
					map[lane * 4 + dx + y * width()] = Terrain.WALL_DECO;
				}
			}
		}

		Painter.fill(this, ROOM_LEFT - 1, ROOM_TOP - 1, 5, 5, Terrain.HIGH_GRASS);
		Painter.fill(this, ROOM_LEFT, ROOM_TOP, 3, 3, Terrain.EMPTY);
		boolean[] waterPatch = Patch.generate(width(), height(), 0.45f, 6, true);
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && waterPatch[cell]) map[cell] = Terrain.WATER;
			else if (map[cell] == Terrain.EMPTY && Random.Int(10) == 0) map[cell] = Terrain.EMPTY_DECO;
		}

		map[ENTRANCE] = Terrain.PEDESTAL;
		map[arenaExit] = Terrain.LOCKED_EXIT;
		completed = completed || journalCompleted();
		locked = !completed;
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		transitions.add(new LevelTransition(this, arenaExit,
				LevelTransition.Type.BRANCH_EXIT,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_MAGIC_CAVE, width(), height(), map));
		return true;
	}

	@Override
	protected void createMobs() {
		if (completed || journalCompleted()) return;
		Dragonking boss = new Dragonking();
		boss.pos = safeSpawnCell(ENTRANCE - 4 * width());
		boss.state = boss.HUNTING;
		mobs().add(boss);
		bossStage = 0;
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
		return safeSpawnCell(ENTRANCE);
	}

	public int safeSpawnCell(int preferred) {
		if (insideMap(preferred) && passable[preferred] && Actor.findChar(preferred) == null) return preferred;
		PathFinder.buildDistanceMap(preferred, passable);
		int best = -1;
		int bestDistance = Integer.MAX_VALUE;
		for (int cell = 0; cell < length(); cell++) {
			if (passable[cell] && Actor.findChar(cell) == null && PathFinder.distance[cell] < bestDistance) {
				best = cell;
				bestDistance = PathFinder.distance[cell];
			}
		}
		if (best >= 0) return best;
		for (int cell = 0; cell < length(); cell++) {
			if (passable[cell] && Actor.findChar(cell) == null) return cell;
		}
		return ENTRANCE;
	}

	public void advance(Mob defeated, Class<? extends BossRushBoss> next) {
		GameScene.bossSlain();
		bossStage++;
		if (next == null) {
			completeRush();
			return;
		}
		BossRushBoss.spawn(next, safeSpawnCell(defeated.pos));
	}

	private void completeRush() {
		completed = true;
		if (AdventureJournal.destinationForBranch(Dungeon.branch) == 21) {
			AdventureJournal.complete(21);
		}
		locked = false;
		Level.set(arenaExit, Terrain.UNLOCKED_EXIT, this);
		for (pd.tiles.CustomTilemap visual : customTiles) {
			if (visual instanceof SpsLegacyLevelVisual) {
				((SpsLegacyLevelVisual) visual).updateTerrainCell(arenaExit, Terrain.UNLOCKED_EXIT);
			}
		}
		GameScene.updateMap(arenaExit);
	}

	private boolean journalCompleted() {
		if (Dungeon.hero == null || AdventureJournal.destinationForBranch(Dungeon.branch) != 21) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		return journal != null && journal.isCompleted(21);
	}

	public int bossStage() {
		return bossStage;
	}

	public int arenaExit() {
		return arenaExit;
	}

	public boolean completed() {
		return completed;
	}

	private static final String BOSS_STAGE = "boss_stage";
	private static final String ARENA_EXIT = "arena_exit";
	private static final String COMPLETED = "completed";
	private static final String LEGACY_STAIRS = "stairs";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BOSS_STAGE, bossStage);
		bundle.put(ARENA_EXIT, arenaExit);
		bundle.put(COMPLETED, completed);
		bundle.put(LEGACY_STAIRS, arenaExit);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		arenaExit = bundle.contains(ARENA_EXIT) ? bundle.getInt(ARENA_EXIT) : findArenaExit();
		if (!isArenaExit(arenaExit)) arenaExit = findArenaExit();
		bossStage = bundle.contains(BOSS_STAGE) ? bundle.getInt(BOSS_STAGE) : inferBossStage();
		bossStage = Math.max(0, Math.min(BOSS_SEQUENCE.length, bossStage));
		completed = (bundle.contains(COMPLETED) && bundle.getBoolean(COMPLETED))
				|| arenaExit >= 0 && map[arenaExit] == Terrain.UNLOCKED_EXIT
				|| journalCompleted();
		locked = !completed;
	}

	private int findArenaExit() {
		for (int cell = 0; cell < length(); cell++) {
			if (isArenaExit(cell)) return cell;
		}
		return -1;
	}

	private boolean isArenaExit(int cell) {
		return cell >= 0 && cell < length()
				&& (map[cell] == Terrain.LOCKED_EXIT || map[cell] == Terrain.UNLOCKED_EXIT);
	}

	private int inferBossStage() {
		for (Mob mob : mobs()) {
			for (int stage = 0; stage < BOSS_SEQUENCE.length; stage++) {
				if (mob.getClass() == BOSS_SEQUENCE[stage]) return stage;
			}
		}
		return 0;
	}
}
