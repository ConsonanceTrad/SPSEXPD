/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Zot;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import pd.scenes.GameScene;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

/** The original randomly carved 48x48 prison which contains Zot. */
public class ZotBossLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	private static final int ROOM_LEFT = WIDTH / 2 - 2;
	private static final int ROOM_RIGHT = WIDTH / 2 + 2;
	private static final int ROOM_TOP = HEIGHT / 2 - 2;
	private static final int ROOM_BOTTOM = HEIGHT / 2 + 2;

	private int arenaDoor;
	private int entranceCell;
	private boolean enteredArena;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
		viewDistance = 6;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		Arrays.fill(map, Terrain.WALL);

		for (int i = 0; i < 8; i++) {
			int left = Random.Int(2) == 0
					? Random.Int(1, ROOM_LEFT - 3) : ROOM_LEFT - 3;
			int right = left < ROOM_LEFT - 3
					? ROOM_RIGHT + 3 : Random.Int(ROOM_RIGHT + 3, WIDTH - 1);
			int top = Random.Int(2) == 0
					? Random.Int(2, ROOM_TOP - 3) : ROOM_TOP - 3;
			int bottom = top < ROOM_TOP - 3
					? ROOM_BOTTOM + 3 : Random.Int(ROOM_BOTTOM + 3, HEIGHT - 1);
			Painter.fill(this, left, top, right - left + 1, bottom - top + 1, Terrain.EMPTY);
		}

		Painter.fill(this, ROOM_LEFT - 1, ROOM_TOP - 1, 7, 7, Terrain.WALL);
		Painter.fill(this, ROOM_LEFT, ROOM_TOP + 1, 5, 4, Terrain.EMPTY);
		Painter.fill(this, ROOM_LEFT, ROOM_TOP, 5, 1, Terrain.TRAP);

		arenaDoor = Random.Int(ROOM_LEFT, ROOM_RIGHT) + (ROOM_BOTTOM + 1) * width();
		map[arenaDoor] = Terrain.DOOR;
		entranceCell = Random.Int(ROOM_LEFT + 1, ROOM_RIGHT)
				+ Random.Int(ROOM_TOP + 1, ROOM_BOTTOM) * width();
		map[entranceCell] = Terrain.PEDESTAL;
		transitions.add(new LevelTransition(this, entranceCell,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));

		for (int cell = width() + 1; cell < length() - width() - 1; cell++) {
			if (map[cell] != Terrain.EMPTY) continue;
			int adjacentWalls = 0;
			if (map[cell + 1] == Terrain.WALL) adjacentWalls++;
			if (map[cell - 1] == Terrain.WALL) adjacentWalls++;
			if (map[cell + width()] == Terrain.WALL) adjacentWalls++;
			if (map[cell - width()] == Terrain.WALL) adjacentWalls++;
			if (Random.Int(8) <= adjacentWalls) map[cell] = Terrain.EMPTY_DECO;
		}

		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_MAGIC_CAVE, width(), height(), map));
		locked = !Dungeon.zotKilled;
		return true;
	}

	@Override protected void createMobs() { }
	@Override protected void createItems() { }
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return safeBossCell(entranceCell); }

	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		// Zot's prison can only be left by using the same Palantir that opened it.
		return false;
	}

	@Override
	public void pressCell(int cell) {
		super.pressCell(cell);
		if (!enteredArena && !Dungeon.zotKilled && Dungeon.hero != null
				&& Dungeon.hero.pos == cell && outsideEntranceRoom(cell)) {
			enteredArena = true;
			spawnZot(cell);
			Dungeon.observe();
		}
	}

	private boolean outsideEntranceRoom(int cell) {
		int x = cell % width();
		int y = cell / width();
		return x < ROOM_LEFT - 1 || x > ROOM_RIGHT + 1
				|| y < ROOM_TOP - 1 || y > ROOM_BOTTOM + 1;
	}

	private void spawnZot(int preferred) {
		Zot zot = new Zot();
		zot.pos = safeBossCell(preferred);
		zot.state = zot.HUNTING;
		GameScene.add(zot);
		zot.notice();
	}

	public int safeBossCell(int preferred) {
		if (isFreeOutsideCell(preferred) && !heroCanSee(preferred)) return preferred;
		ArrayList<Integer> fallback = new ArrayList<>();
		for (int tries = 0; tries < 300; tries++) {
			int cell = Random.Int(length());
			if (isFreeOutsideCell(cell)) {
				if (!heroCanSee(cell)) return cell;
				fallback.add(cell);
			}
		}
		if (!fallback.isEmpty()) return Random.element(fallback);
		for (int cell = 0; cell < length(); cell++) {
			if (isFreeOutsideCell(cell)) return cell;
		}
		return entranceCell;
	}

	private boolean isFreeOutsideCell(int cell) {
		return insideMap(cell) && outsideEntranceRoom(cell) && passable[cell]
				&& Actor.findChar(cell) == null;
	}

	private boolean heroCanSee(int cell) {
		return heroFOV != null && cell >= 0 && cell < heroFOV.length && heroFOV[cell];
	}

	public void zotDefeated() {
		locked = false;
		Dungeon.zotKilled = true;
		GameScene.bossSlain();
	}

	@Override public String tilesTex() { return Assets.Environment.TILES_HALLS; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_CITY; }

	private static final String DOOR = "door";
	private static final String ENTRANCE = "entrance";
	private static final String ENTERED = "entered";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DOOR, arenaDoor);
		bundle.put(ENTRANCE, entranceCell);
		bundle.put(ENTERED, enteredArena);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		arenaDoor = bundle.getInt(DOOR);
		entranceCell = bundle.getInt(ENTRANCE);
		enteredArena = bundle.getBoolean(ENTERED);
		locked = !Dungeon.zotKilled;
		if (enteredArena && !Dungeon.zotKilled) {
			boolean found = false;
			for (Mob mob : mobs()) {
				if (mob instanceof Zot) {
					found = true;
					break;
				}
			}
			if (!found) {
				Zot zot = new Zot();
				zot.pos = safeBossCell(entranceCell);
				zot.state = zot.HUNTING;
				mobs().add(zot);
			}
		}
	}
}
