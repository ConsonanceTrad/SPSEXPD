/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Fiend;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldOrc;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ShadowYog;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Legacy depth-35 infestation arena, including its ten Shadow Yog encounter. */
public class InfestBossLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ROOM_LEFT = WIDTH / 2 - 2;
	public static final int ROOM_RIGHT = WIDTH / 2 + 2;
	public static final int ROOM_TOP = HEIGHT / 2 - 2;
	public static final int ROOM_BOTTOM = HEIGHT / 2 + 2;
	public static final int SHADOW_YOG_COUNT = 10;
	public static final int INITIAL_MOB_COUNT = 20;

	private int arenaDoor;
	private boolean enteredArena;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
		viewDistance = 6;
	}

	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_CAVES_LEGACY; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_CAVES; }

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		int topMost = Integer.MAX_VALUE;
		int legacyExit = -1;
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
				right = Random.Int(ROOM_RIGHT + 3, WIDTH - 1);
			}
			if (Random.Int(2) == 0) {
				top = Random.Int(2, ROOM_TOP - 3);
				bottom = ROOM_BOTTOM + 3;
			} else {
				top = ROOM_LEFT - 3;
				bottom = Random.Int(ROOM_TOP + 3, HEIGHT - 1);
			}
			Painter.fill(this, left, top, right - left + 1, bottom - top + 1, Terrain.EMPTY);
			if (top < topMost) {
				topMost = top;
				legacyExit = Random.Int(left, right) + (top - 1) * WIDTH;
			}
		}
		if (legacyExit < 0) return false;
		map[legacyExit] = Terrain.WALL;

		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Int(20) == 0) {
				setTrap(new PoisonDartTrap().reveal(), cell);
				map[cell] = Terrain.TRAP;
			}
		}

		Painter.fill(this, ROOM_LEFT - 1, ROOM_TOP - 1,
				ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL);
		Painter.fill(this, ROOM_LEFT, ROOM_TOP + 1,
				ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP, Terrain.EMPTY);
		Painter.fill(this, ROOM_LEFT, ROOM_TOP,
				ROOM_RIGHT - ROOM_LEFT + 1, 1, Terrain.INACTIVE_TRAP);

		arenaDoor = Random.Int(ROOM_LEFT, ROOM_RIGHT) + (ROOM_BOTTOM + 1) * WIDTH;
		map[arenaDoor] = Terrain.DOOR;
		int entrance = Random.Int(ROOM_LEFT + 1, ROOM_RIGHT - 1)
				+ Random.Int(ROOM_TOP + 1, ROOM_BOTTOM - 1) * WIDTH;
		map[entrance] = Terrain.PEDESTAL;
		transitions.add(new LevelTransition(this, entrance,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		for (com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap trap
				: traps.valueList().toArray(new com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap[0])) {
			if (map[trap.pos] != Terrain.TRAP) traps.remove(trap.pos);
		}

		decorateLegacyMap();
		return true;
	}

	private void decorateLegacyMap() {
		for (int cell = WIDTH + 1; cell < length() - WIDTH; cell++) {
			if (map[cell] != Terrain.EMPTY) continue;
			int walls = 0;
			if (map[cell + 1] == Terrain.WALL) walls++;
			if (map[cell - 1] == Terrain.WALL) walls++;
			if (map[cell + WIDTH] == Terrain.WALL) walls++;
			if (map[cell - WIDTH] == Terrain.WALL) walls++;
			if (Random.Int(8) <= walls) map[cell] = Terrain.EMPTY_DECO;
		}
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.WALL && Random.Int(8) == 0) map[cell] = Terrain.WALL_DECO;
		}
	}

	@Override
	protected void createMobs() {
		for (int i = 0; i < INITIAL_MOB_COUNT; i++) {
			Mob mob = Random.Int(2) == 0 ? new GoldOrc() : new Fiend();
			int cell = randomRespawnCell(mob);
			if (cell < 0) break;
			mob.pos = cell;
			mobs.add(mob);
		}
	}

	@Override protected void createItems() { }
	@Override public Mob createMob() { return Random.Int(2) == 0 ? new GoldOrc() : new Fiend(); }
	@Override public Actor addRespawner() { return null; }

	@Override
	public int randomRespawnCell(Char ch) {
		for (int tries = 0; tries < 500; tries++) {
			int cell = Random.Int(length());
			if (passable[cell] && outsideEntranceRoom(cell) && Actor.findChar(cell) == null) return cell;
		}
		return -1;
	}

	@Override
	public void pressCell(int cell) {
		super.pressCell(cell);
		if (!enteredArena && Dungeon.hero != null && Dungeon.hero.pos == cell && outsideEntranceRoom(cell)) {
			enteredArena = true;
			spawnShadowYogs();
			GameScene.updateMap(arenaDoor);
			Dungeon.observe();
		}
	}

	private void spawnShadowYogs() {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < length(); cell++) {
			if (passable[cell] && outsideEntranceRoom(cell) && Actor.findChar(cell) == null
					&& (heroFOV == null || !heroFOV[cell])) candidates.add(cell);
		}
		if (candidates.size() < SHADOW_YOG_COUNT) {
			for (int cell = 0; cell < length(); cell++) {
				if (passable[cell] && outsideEntranceRoom(cell) && Actor.findChar(cell) == null
						&& !candidates.contains(cell)) candidates.add(cell);
			}
		}
		Random.shuffle(candidates);
		for (int i = 0; i < Math.min(SHADOW_YOG_COUNT, candidates.size()); i++) {
			ShadowYog boss = new ShadowYog();
			boss.pos = candidates.get(i);
			boss.state = boss.SLEEPING;
			GameScene.add(boss);
		}
	}

	private boolean outsideEntranceRoom(int cell) {
		int x = cell % WIDTH;
		int y = cell / WIDTH;
		return x < ROOM_LEFT - 1 || x > ROOM_RIGHT + 1
				|| y < ROOM_TOP - 1 || y > ROOM_BOTTOM + 1;
	}

	private boolean completed() {
		if (Dungeon.hero == null) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		return journal != null && journal.isCompleted(9);
	}

	private static final String DOOR = "door";
	private static final String ENTERED = "entered";
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DOOR, arenaDoor);
		bundle.put(ENTERED, enteredArena);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		arenaDoor = bundle.getInt(DOOR);
		enteredArena = bundle.getBoolean(ENTERED);
		if (enteredArena && !completed()) {
			boolean found = false;
			for (Mob mob : mobs) {
				if (mob instanceof ShadowYog) { found = true; break; }
			}
			if (!found) {
				// A damaged save must remain winnable; the encounter is recreated on entry.
				enteredArena = false;
			}
		}
	}
}
