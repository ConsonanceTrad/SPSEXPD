/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Hybrid;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpsDM300;
import pd.actors.mobs.SpiderQueen;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import pd.levels.traps.ToxicTrap;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;
import pd.tiles.custom.SpsLegacyLevelVisual;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** SPS-PD 0.9.8's procedural 48x48 caves boss arena. */
public class SpsCavesBossLevel extends Level {

	static final int WIDTH = 48;
	static final int HEIGHT = 48;
	static final int ROOM_LEFT = 22;
	static final int ROOM_RIGHT = 26;
	static final int ROOM_TOP = 22;
	static final int ROOM_BOTTOM = 26;

	static final int HYBRID = 0;
	static final int DM300 = 1;
	static final int SPIDER_QUEEN = 2;

	private static final String ARENA_DOOR = "arena_door";
	private static final String ENTERED = "entered";
	private static final String BOSS_VARIANT = "boss_variant";

	private int arenaDoor;
	private boolean enteredArena;
	private int bossVariant = -1;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
		viewDistance = 6;
	}

	@Override
	protected boolean build() {
		selectBoss();
		setSize(WIDTH, HEIGHT);

		int topMost = Integer.MAX_VALUE;
		int exit = -1;
		for (int i = 0; i < 8; i++) {
			int left = Random.Int(2) == 0
					? Random.Int(1, ROOM_LEFT - 3)
					: ROOM_LEFT - 3;
			int right = left == ROOM_LEFT - 3
					? Random.Int(ROOM_RIGHT + 3, WIDTH - 1)
					: ROOM_RIGHT + 3;
			int top = Random.Int(2) == 0
					? Random.Int(2, ROOM_TOP - 3)
					: ROOM_LEFT - 3;
			int bottom = top == ROOM_LEFT - 3
					? Random.Int(ROOM_TOP + 3, HEIGHT - 1)
					: ROOM_BOTTOM + 3;

			Painter.fill(this, left, top, right - left + 1, bottom - top + 1, Terrain.EMPTY);
			if (top < topMost) {
				topMost = top;
				exit = Random.Int(left, right) + (top - 1) * WIDTH;
			}
		}

		if (exit < 0 || !insideMap(exit)) return false;
		map[exit] = Terrain.LOCKED_EXIT;

		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Int(12) == 0) {
				map[cell] = Terrain.INACTIVE_TRAP;
				Trap trap = new ToxicTrap().reveal();
				trap.active = false;
				setTrap(trap, cell);
			}
		}

		Painter.fill(this, ROOM_LEFT - 1, ROOM_TOP - 1,
				ROOM_RIGHT - ROOM_LEFT + 3, ROOM_BOTTOM - ROOM_TOP + 3, Terrain.WALL);
		Painter.fill(this, ROOM_LEFT, ROOM_TOP + 1,
				ROOM_RIGHT - ROOM_LEFT + 1, ROOM_BOTTOM - ROOM_TOP, Terrain.EMPTY);
		Painter.fill(this, ROOM_LEFT, ROOM_TOP,
				ROOM_RIGHT - ROOM_LEFT + 1, 1, Terrain.INACTIVE_TRAP);

		// The old order could leave invisible trap objects buried under the entrance-room walls.
		for (int y = ROOM_TOP - 1; y <= ROOM_BOTTOM + 1; y++) {
			for (int x = ROOM_LEFT - 1; x <= ROOM_RIGHT + 1; x++) {
				int cell = x + y * WIDTH;
				if (map[cell] != Terrain.INACTIVE_TRAP) traps.remove(cell);
			}
		}

		arenaDoor = Random.Int(ROOM_LEFT, ROOM_RIGHT) + (ROOM_BOTTOM + 1) * WIDTH;
		map[arenaDoor] = Terrain.DOOR;
		int entrance = Random.Int(ROOM_LEFT + 1, ROOM_RIGHT - 1)
				+ Random.Int(ROOM_TOP + 1, ROOM_BOTTOM - 1) * WIDTH;
		map[entrance] = Terrain.ENTRANCE;

		boolean[] patch = Patch.generate(WIDTH, HEIGHT, 0.45f, 6, false);
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && patch[cell]) map[cell] = Terrain.WATER;
		}
		decorateLegacy(entrance);
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_CAVES_LEGACY, width(), height(), map));

		transitions.add(new LevelTransition(this, entrance,
				LevelTransition.Type.REGULAR_ENTRANCE,
				Dungeon.depth - 1, Dungeon.branch, LevelTransition.Type.REGULAR_EXIT));
		transitions.add(new LevelTransition(this, exit,
				LevelTransition.Type.REGULAR_EXIT,
				Dungeon.depth + 1, Dungeon.branch, LevelTransition.Type.REGULAR_ENTRANCE));
		locked = false;
		return true;
	}

	private void decorateLegacy(int entrance) {
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
		ArrayList<Integer> signs = new ArrayList<>();
		for (int y = ROOM_TOP; y < ROOM_BOTTOM; y++) {
			for (int x = ROOM_LEFT; x < ROOM_RIGHT; x++) {
				int cell = x + y * WIDTH;
				if (cell != entrance && map[cell] != Terrain.INACTIVE_TRAP) signs.add(cell);
			}
		}
		if (!signs.isEmpty()) map[Random.element(signs)] = Terrain.SIGN;
	}

	@Override protected void createMobs() { }
	@Override protected void createItems() { }
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }

	@Override
	public void pressCell(int cell) {
		super.pressCell(cell);
		if (!enteredArena && Dungeon.hero != null && Dungeon.hero.pos == cell && outsideEntranceRoom(cell)) {
			enteredArena = true;
			seal();
			spawnBoss(false);
			Dungeon.observe();
		}
	}

	private void spawnBoss(boolean restoring) {
		Mob boss = createLegacyBoss();
		ArrayList<Integer> hidden = new ArrayList<>();
		ArrayList<Integer> fallback = new ArrayList<>();
		for (int cell = 0; cell < length(); cell++) {
			if (!passable[cell] || !outsideEntranceRoom(cell) || Actor.findChar(cell) != null) continue;
			fallback.add(cell);
			if (heroFOV == null || !heroFOV[cell]) hidden.add(cell);
		}
		ArrayList<Integer> candidates = hidden.isEmpty() ? fallback : hidden;
		if (candidates.isEmpty()) {
			for (int cell = 0; cell < length(); cell++) {
				if (passable[cell] && Actor.findChar(cell) == null) candidates.add(cell);
			}
		}
		if (candidates.isEmpty()) {
			if (!restoring) unseal();
			return;
		}
		boss.pos = restoring ? farthestFromEntrance(candidates) : Random.element(candidates);
		boss.state = boss.HUNTING;
		if (restoring) mobs.add(boss);
		else {
			GameScene.add(boss);
			boss.notice();
		}
	}

	private int farthestFromEntrance(ArrayList<Integer> cells) {
		int entrance = entrance();
		int best = cells.get(0);
		int bestDistance = -1;
		for (int cell : cells) {
			int distance = distance(entrance, cell);
			if (distance > bestDistance) {
				best = cell;
				bestDistance = distance;
			}
		}
		return best;
	}

	@Override
	public void seal() {
		if (locked) return;
		super.seal();
		set(arenaDoor, Terrain.WALL);
		set(entrance(), Terrain.WALL_DECO);
		GameScene.updateMap(arenaDoor);
		GameScene.updateMap(entrance());
		GameScene.ripple(entrance());
		CellEmitter.get(arenaDoor).start(Speck.factory(Speck.ROCK), 0.07f, 10);
		Camera.main.shake(3, 0.7f);
		Sample.INSTANCE.play(Assets.Sounds.ROCKS);
	}

	@Override
	public void unseal() {
		if (!locked) return;
		super.unseal();
		set(arenaDoor, Terrain.EMPTY_DECO);
		set(entrance(), Terrain.ENTRANCE);
		set(exit(), Terrain.EXIT);
		GameScene.updateMap(arenaDoor);
		GameScene.updateMap(entrance());
		GameScene.updateMap(exit());
		Dungeon.observe();
	}

	boolean outsideEntranceRoom(int cell) {
		int x = cell % WIDTH;
		int y = cell / WIDTH;
		return x < ROOM_LEFT - 1 || x > ROOM_RIGHT + 1 || y < ROOM_TOP - 1 || y > ROOM_BOTTOM + 1;
	}

	Mob createLegacyBoss() {
		selectBoss();
		switch (bossVariant) {
			case HYBRID: return new Hybrid();
			case DM300: return new SpsDM300();
			default: return new SpiderQueen();
		}
	}

	private void selectBoss() {
		if (bossVariant >= 0) return;
		if (Random.Int(3) == 1) bossVariant = HYBRID;
		else if (Random.Int(2) == 1) bossVariant = DM300;
		else bossVariant = SPIDER_QUEEN;
	}

	private boolean isFightActor(Mob mob) {
		selectBoss();
		if (bossVariant == HYBRID) return mob instanceof Hybrid;
		if (bossVariant == DM300) return mob instanceof SpsDM300 || mob instanceof SpsDM300.Tower;
		return mob instanceof SpiderQueen;
	}

	int bossVariantForTesting() {
		selectBoss();
		return bossVariant;
	}

	int arenaDoorForTesting() { return arenaDoor; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		selectBoss();
		bundle.put(ARENA_DOOR, arenaDoor);
		bundle.put(ENTERED, enteredArena);
		bundle.put(BOSS_VARIANT, bossVariant);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		arenaDoor = bundle.getInt(ARENA_DOOR);
		if (!insideMap(arenaDoor) || arenaDoor / WIDTH != ROOM_BOTTOM + 1) {
			arenaDoor = ROOM_LEFT + (ROOM_BOTTOM + 1) * WIDTH;
			for (int x = ROOM_LEFT; x < ROOM_RIGHT; x++) {
				int cell = x + (ROOM_BOTTOM + 1) * WIDTH;
				if (map[cell] == Terrain.DOOR || map[cell] == Terrain.EMPTY_DECO) { arenaDoor = cell; break; }
			}
		}
		enteredArena = bundle.getBoolean(ENTERED);
		if (bundle.contains(BOSS_VARIANT)) bossVariant = bundle.getInt(BOSS_VARIANT);
		else {
			bossVariant = HYBRID;
			for (Mob mob : mobs) {
				if (mob instanceof SpsDM300 || mob instanceof SpsDM300.Tower) bossVariant = DM300;
				else if (mob instanceof SpiderQueen) bossVariant = SPIDER_QUEEN;
			}
		}
		if (bossVariant < HYBRID || bossVariant > SPIDER_QUEEN) bossVariant = HYBRID;
		if (enteredArena && locked) {
			boolean found = false;
			for (Mob mob : mobs) if (isFightActor(mob)) { found = true; break; }
			if (!found) spawnBoss(true);
		}
	}

	@Override public String tilesTex() { return Assets.Environment.TILES_CAVES; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_CAVES; }

	@Override
	public String tileName(int tile) {
		switch (tile) {
			case Terrain.GRASS: return pd.messages.Messages.get(CavesLevel.class, "grass_name");
			case Terrain.HIGH_GRASS: return pd.messages.Messages.get(CavesLevel.class, "high_grass_name");
			case Terrain.WATER: return pd.messages.Messages.get(CavesLevel.class, "water_name");
			default: return super.tileName(tile);
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.ENTRANCE:
			case Terrain.ENTRANCE_SP:
				return pd.messages.Messages.get(CavesLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return pd.messages.Messages.get(CavesLevel.class, "exit_desc");
			case Terrain.HIGH_GRASS:
				return pd.messages.Messages.get(CavesLevel.class, "high_grass_desc");
			case Terrain.WALL_DECO:
				return pd.messages.Messages.get(CavesLevel.class, "wall_deco_desc");
			case Terrain.BOOKSHELF:
				return pd.messages.Messages.get(CavesLevel.class, "bookshelf_desc");
			default: return super.tileDesc(tile);
		}
	}

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.SPS_GAME, true);
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		CavesLevel.addCavesVisuals(this, visuals);
		return visuals;
	}
}
