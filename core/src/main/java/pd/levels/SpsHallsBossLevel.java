/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Yog;
import pd.effects.CellEmitter;
import pd.effects.particles.FlameParticle;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import pd.scenes.GameScene;
import pd.tiles.custom.SpsLegacyLevelVisual;
import watabou.noosa.Group;
import watabou.noosa.audio.Music;
import watabou.utils.Bundle;
import watabou.utils.PathFinder;
import watabou.utils.Random;

import java.util.ArrayList;

/** SPS-PD 0.9.8's 48x48 final boss halls. */
public class SpsHallsBossLevel extends Level {
	static final int WIDTH = 48;
	static final int HEIGHT = 48;
	static final int ROOM_LEFT = WIDTH / 2 - 1;
	static final int ROOM_RIGHT = WIDTH / 2 + 1;
	static final int ROOM_TOP = HEIGHT / 2 - 1;
	static final int ROOM_BOTTOM = HEIGHT / 2 + 1;
	static final int ENTRANCE = 24 + 24 * WIDTH;

	private static final String ENTERED = "entered";
	private boolean enteredArena;

	{
		color1 = 0x801500;
		color2 = 0xa68521;
		viewDistance = 5;
	}

	@Override protected boolean build() {
		setSize(WIDTH, HEIGHT);
		int exit = -1;
		for (int i = 0; i < 5; i++) {
			int top = Random.IntRange(2, ROOM_TOP - 1);
			// The old RNG accepted reversed bounds here and produced 24..26.
			int bottom = 24 + Random.Int(3);
			Painter.fill(this, 2 + i * 4, top, 4, bottom - top + 1, Terrain.EMPTY);
			if (i == 2) exit = i * 4 + 3 + (top - 1) * WIDTH;
			for (int j = 0; j < 4; j++) if (Random.Int(2) == 0) {
				int y = Random.IntRange(top + 1, bottom - 1);
				map[i * 4 + j + y * WIDTH] = Terrain.WALL_DECO;
			}
		}
		if (exit < 0 || !insideMap(exit)) return false;
		map[exit] = Terrain.LOCKED_EXIT;

		Painter.fill(this, ROOM_LEFT - 1, ROOM_TOP - 1, 5, 5, Terrain.WALL);
		Painter.fill(this, ROOM_LEFT, ROOM_TOP, 3, 3, Terrain.EMPTY);
		map[ENTRANCE] = Terrain.ENTRANCE;
		boolean[] waterPatch = Patch.generate(WIDTH, HEIGHT, 0.45f, 6, false);
		for (int cell = 0; cell < length(); cell++) if (map[cell] == Terrain.EMPTY && waterPatch[cell]) map[cell] = Terrain.WATER;
		for (int cell = 0; cell < length(); cell++) if (map[cell] == Terrain.EMPTY && Random.Int(10) == 0) map[cell] = Terrain.EMPTY_DECO;
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_HALLS_LEGACY, width(), height(), map));

		transitions.add(new LevelTransition(this, ENTRANCE, LevelTransition.Type.REGULAR_ENTRANCE,
				Dungeon.depth - 1, Dungeon.branch, LevelTransition.Type.REGULAR_EXIT));
		transitions.add(new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT,
				Dungeon.depth + 1, Dungeon.branch, LevelTransition.Type.REGULAR_ENTRANCE));
		locked = false;
		return true;
	}

	@Override protected void createMobs() { }
	@Override protected void createItems() { }
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = ENTRANCE + offset;
			if (insideMap(cell) && passable[cell] && Actor.findChar(cell) == null) cells.add(cell);
		}
		return cells.isEmpty() ? -1 : Random.element(cells);
	}

	@Override public void pressCell(int cell) {
		super.pressCell(cell);
		if (!enteredArena && Dungeon.hero != null && Dungeon.hero.pos == cell && cell != ENTRANCE) {
			enteredArena = true;
			seal();
			openStartingRoom();
			spawnBoss(false);
			Dungeon.observe();
		}
	}

	private void openStartingRoom() {
		for (int x = ROOM_LEFT - 1; x <= ROOM_RIGHT + 1; x++) {
			doMagic((ROOM_TOP - 1) * WIDTH + x);
			doMagic((ROOM_BOTTOM + 1) * WIDTH + x);
		}
		for (int y = ROOM_TOP; y <= ROOM_BOTTOM; y++) {
			doMagic(y * WIDTH + ROOM_LEFT - 1);
			doMagic(y * WIDTH + ROOM_RIGHT + 1);
		}
		doMagic(ENTRANCE);
	}

	private void doMagic(int cell) {
		set(cell, Terrain.EMPTY_SP);
		GameScene.updateMap(cell);
		CellEmitter.get(cell).start(FlameParticle.FACTORY, 0.1f, 3);
	}

	private void spawnBoss(boolean restoring) {
		ArrayList<Integer> hidden = new ArrayList<>();
		ArrayList<Integer> fallback = new ArrayList<>();
		for (int cell = 0; cell < length(); cell++) {
			if (!passable[cell] || Actor.findChar(cell) != null || cell == ENTRANCE) continue;
			fallback.add(cell);
			if (heroFOV == null || !heroFOV[cell]) hidden.add(cell);
		}
		ArrayList<Integer> cells = hidden.isEmpty() ? fallback : hidden;
		if (cells.isEmpty()) { unseal(); return; }
		Yog boss = new Yog();
		boss.pos = restoring ? farthest(cells) : Random.element(cells);
		boss.state = boss.HUNTING;
		if (restoring) mobs.add(boss);
		else { GameScene.add(boss); boss.notice(); boss.spawnFists(); }
	}

	private int farthest(ArrayList<Integer> cells) {
		int best = cells.get(0), max = -1;
		for (int cell : cells) { int d = distance(ENTRANCE, cell); if (d > max) { max = d; best = cell; } }
		return best;
	}

	@Override public void seal() {
		if (locked) return;
		super.seal();
		set(ENTRANCE, Terrain.WALL_DECO);
		GameScene.updateMap(ENTRANCE);
		GameScene.ripple(ENTRANCE);
	}

	@Override public void unseal() {
		if (!locked) return;
		super.unseal();
		set(ENTRANCE, Terrain.ENTRANCE);
		set(exit(), Terrain.EXIT);
		GameScene.updateMap(ENTRANCE);
		GameScene.updateMap(exit());
		Dungeon.observe();
	}

	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(ENTERED, enteredArena); }
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		enteredArena = bundle.getBoolean(ENTERED);
		if (enteredArena && locked) {
			boolean found = false;
			for (Mob mob : mobs) if (mob instanceof Yog) { found = true; break; }
			if (!found) spawnBoss(true);
		}
	}

	@Override public String tilesTex() { return Assets.Environment.TILES_HALLS; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_HALLS; }
	@Override public void playLevelMusic() { Music.INSTANCE.play(Assets.Music.SPS_GAME, true); }
	@Override public Group addVisuals() { super.addVisuals(); HallsLevel.addHallsVisuals(this, visuals); return visuals; }
}
