/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

/** Shared geometry used by the five SPS-PD challenge-book region arenas. */
abstract class SpsRegionChallengeLevel extends Level {

	static final int LEGACY_WIDTH = 48;
	static final int LEGACY_HEIGHT = 48;
	private static final int ROOM_LEFT = LEGACY_WIDTH / 2 - 2;
	private static final int ROOM_RIGHT = LEGACY_WIDTH / 2 + 2;
	private static final int ROOM_TOP = LEGACY_HEIGHT / 2 - 2;
	private static final int ROOM_BOTTOM = LEGACY_HEIGHT / 2 + 2;

	private int legacyEntrance;
	private int legacyExit;

	SpsRegionChallengeLevel() {
		cleared = true;
	}

	@Override
	protected final boolean build() {
		setSize(LEGACY_WIDTH, LEGACY_HEIGHT);
		int topMost = Integer.MAX_VALUE;

		for (int i = 0; i < 8; i++) {
			int left;
			int right;
			int top;
			int bottom;
			if (Random.Int(2) == 0) {
				left = Random.Int(1, ROOM_LEFT - 3);
				right = ROOM_RIGHT + 3;
			} else {
				left = ROOM_LEFT - 3;
				right = Random.Int(ROOM_RIGHT + 3, width() - 1);
			}
			if (Random.Int(2) == 0) {
				top = Random.Int(2, ROOM_TOP - 3);
				bottom = ROOM_BOTTOM + 3;
			} else {
				// This apparently odd coordinate is present in SPS-PD 0.9.8.
				top = ROOM_LEFT - 3;
				bottom = Random.Int(ROOM_TOP + 3, height() - 1);
			}

			Painter.fill(this, left, top, right - left + 1, bottom - top + 1, Terrain.EMPTY);
			if (top < topMost) {
				topMost = top;
				legacyExit = Random.Int(left, right) + (top - 1) * width();
			}
		}

		map[legacyExit] = Terrain.WALL;
		Painter.fill(this, ROOM_LEFT, ROOM_TOP + 1,
				ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP, Terrain.EMPTY);
		legacyEntrance = Random.Int(ROOM_LEFT + 1, ROOM_RIGHT - 1)
				+ Random.Int(ROOM_TOP + 1, ROOM_BOTTOM - 1) * width();

		shapeTerrain();
		decorateTerrain();
		// Legacy decoration could replace the chosen spawn with a blocking shrub or statue.
		map[legacyEntrance] = Terrain.EMPTY;
		map[legacyExit] = Terrain.WALL;
		transitions.add(new LevelTransition(this, legacyEntrance,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		return true;
	}

	protected void shapeTerrain() {
	}

	protected void decorateTerrain() {
		for (int cell = width() + 1; cell < length() - width(); cell++) {
			if (map[cell] != Terrain.EMPTY) continue;
			int walls = 0;
			if (map[cell + 1] == Terrain.WALL) walls++;
			if (map[cell - 1] == Terrain.WALL) walls++;
			if (map[cell + width()] == Terrain.WALL) walls++;
			if (map[cell - width()] == Terrain.WALL) walls++;
			if (Random.Int(8) <= walls) map[cell] = Terrain.EMPTY_DECO;
		}
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.WALL && Random.Int(8) == 0) map[cell] = Terrain.WALL_DECO;
		}
	}

	final int legacyEntranceCell() {
		return legacyEntrance;
	}

	final int legacyExitCell() {
		return legacyExit;
	}

	protected abstract Mob createChallengeMob();
	protected abstract int challengeMobLimit();
	protected boolean challengeMobsRequireWater() { return false; }
	protected float challengeRespawnCooldown() { return 20f; }

	@Override protected void createMobs() {
		for (int i = 0; i < challengeMobLimit(); i++) {
			Mob mob = createChallengeMob();
			int cell = randomPassableCell(mob, challengeMobsRequireWater());
			if (cell == -1) break;
			mob.pos = cell;
			mobs.add(mob);
		}
	}
	@Override protected void createItems() { }
	@Override public Mob createMob() { return createChallengeMob(); }
	@Override public int mobLimit() { return challengeMobLimit(); }
	@Override public float respawnCooldown() { return challengeRespawnCooldown(); }

	@Override public Actor addRespawner() { return super.addRespawner(); }

	@Override public boolean spawnMob(int ignoredDistanceLimit) {
		Mob mob = createChallengeMob();
		if (mob.state != mob.PASSIVE) mob.state = mob.WANDERING;
		int cell = randomPassableCell(mob, challengeMobsRequireWater());
		if (Dungeon.hero != null && Dungeon.hero.isAlive() && cell != -1) {
			mob.pos = cell;
			GameScene.add(mob);
			return true;
		}
		return false;
	}
	@Override public int randomRespawnCell(Char ch) { return randomPassableCell(ch, false); }

	final int randomPassableCell(Char ch, boolean waterOnly) {
		for (int tries = 0; tries < 500; tries++) {
			int cell = Random.Int(length());
			if ((!waterOnly || map[cell] == Terrain.WATER)
					&& passable[cell] && cell != legacyEntrance && findMob(cell) == null
					&& Actor.findChar(cell) == null) return cell;
		}
		for (int cell = 0; cell < length(); cell++) {
			if ((!waterOnly || map[cell] == Terrain.WATER)
					&& passable[cell] && cell != legacyEntrance && findMob(cell) == null
					&& Actor.findChar(cell) == null) return cell;
		}
		return -1;
	}
}
