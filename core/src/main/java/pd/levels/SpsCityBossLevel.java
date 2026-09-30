/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.actors.mobs.LichDancer;
import pd.actors.mobs.ElderAvatar;
import pd.actors.mobs.King;
import pd.actors.mobs.DwarfLich;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import pd.scenes.GameScene;
import pd.tiles.custom.SpsLegacyLevelVisual;
import com.watabou.noosa.Group;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Geometry of SPS-PD 0.9.8's 48x48 city throne-hall boss level. */
public class SpsCityBossLevel extends Level {
	static final int WIDTH = 48;
	static final int HEIGHT = 48;
	static final int TOP = 2;
	static final int HALL_WIDTH = 7;
	static final int HALL_HEIGHT = 15;
	static final int CHAMBER_HEIGHT = 3;
	static final int LEFT = (WIDTH - HALL_WIDTH) / 2;
	static final int CENTER = LEFT + HALL_WIDTH / 2;
	static final int EXIT = (TOP - 1) * WIDTH + CENTER;
	static final int ARENA_DOOR = (TOP + HALL_HEIGHT) * WIDTH + CENTER;
	public static final int WELL = (TOP + 1) * WIDTH + CENTER;

	static final int LICH_DANCER = 0;
	static final int ELDER_AVATAR = 1;
	static final int KING = 2;

	private static final String ENTERED = "entered";
	private static final String BOSS_VARIANT = "boss_variant";
	private boolean enteredArena;
	private int bossVariant = -1;

	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	@Override
	protected boolean build() {
		selectBoss();
		setSize(WIDTH, HEIGHT);
		Painter.fill(this, LEFT, TOP, HALL_WIDTH, HALL_HEIGHT, Terrain.EMPTY);
		Painter.fill(this, CENTER, TOP, 1, HALL_HEIGHT, Terrain.EMPTY_SP);
		map[WELL] = Terrain.WELL;
		map[WELL - 1] = Terrain.EMPTY_SP;
		map[WELL + 1] = Terrain.EMPTY_SP;

		for (int y = TOP + 1; y < TOP + HALL_HEIGHT; y += 2) {
			map[y * WIDTH + CENTER - 2] = Terrain.STATUE_SP;
			map[y * WIDTH + CENTER + 2] = Terrain.STATUE_SP;
		}
		int leftPedestal = pedestal(true);
		int rightPedestal = pedestal(false);
		map[leftPedestal] = map[rightPedestal] = Terrain.PEDESTAL;
		for (int cell = leftPedestal + 1; cell < rightPedestal; cell++) map[cell] = Terrain.EMPTY_SP;

		map[EXIT] = Terrain.LOCKED_EXIT;
		map[ARENA_DOOR] = Terrain.DOOR;
		Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, CHAMBER_HEIGHT, Terrain.EMPTY);
		Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.BOOKSHELF);
		Painter.fill(this, LEFT + HALL_WIDTH - 1, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.BOOKSHELF);

		int entrance = (TOP + HALL_HEIGHT + 2 + Random.Int(CHAMBER_HEIGHT - 1)) * WIDTH
				+ LEFT + Random.Int(HALL_WIDTH - 2);
		map[entrance] = Terrain.ENTRANCE;
		decorateLegacy();
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_CITY_LEGACY, width(), height(), map));

		transitions.add(new LevelTransition(this, entrance,
				LevelTransition.Type.REGULAR_ENTRANCE,
				Dungeon.depth - 1, Dungeon.branch, LevelTransition.Type.REGULAR_EXIT));
		transitions.add(new LevelTransition(this, EXIT,
				LevelTransition.Type.REGULAR_EXIT,
				Dungeon.depth + 1, Dungeon.branch, LevelTransition.Type.REGULAR_ENTRANCE));
		locked = false;
		return true;
	}

	private void decorateLegacy() {
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Int(10) == 0) map[cell] = Terrain.EMPTY_DECO;
			else if (map[cell] == Terrain.WALL && Random.Int(8) == 0) map[cell] = Terrain.WALL_DECO;
		}
		map[ARENA_DOOR + WIDTH + 1] = Terrain.SIGN;
	}

	public static int pedestal(boolean left) {
		return (TOP + HALL_HEIGHT / 2) * WIDTH + CENTER + (left ? -2 : 2);
	}

	boolean outsideEntranceRoom(int cell) { return cell / WIDTH < ARENA_DOOR / WIDTH; }

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
			if (!passable[cell] || !outsideEntranceRoom(cell) || cell == WELL || Actor.findChar(cell) != null) continue;
			fallback.add(cell);
			if (heroFOV == null || !heroFOV[cell]) hidden.add(cell);
		}
		ArrayList<Integer> candidates = hidden.isEmpty() ? fallback : hidden;
		if (candidates.isEmpty()) {
				for (int cell = 0; cell < length(); cell++) if (passable[cell] && cell != WELL && Actor.findChar(cell) == null) candidates.add(cell);
		}
		if (candidates.isEmpty()) {
			unseal();
			return;
		}
		boss.pos = restoring ? farthestFromEntrance(candidates) : Random.element(candidates);
		boss.state = boss.HUNTING;
		if (restoring) mobs.add(boss);
		else { GameScene.add(boss); boss.notice(); }
	}

	private int farthestFromEntrance(ArrayList<Integer> candidates) {
		int best = candidates.get(0);
		int distance = -1;
		for (int cell : candidates) {
			int current = distance(entrance(), cell);
			if (current > distance) { best = cell; distance = current; }
		}
		return best;
	}

	@Override public void seal() {
		if (locked) return;
		super.seal();
		set(ARENA_DOOR, Terrain.WALL);
		set(entrance(), Terrain.WALL_DECO);
		GameScene.updateMap(ARENA_DOOR);
		GameScene.updateMap(entrance());
		GameScene.ripple(entrance());
		CellEmitter.get(ARENA_DOOR).start(Speck.factory(Speck.ROCK), 0.07f, 10);
		Camera.main.shake(3, 0.7f);
		Sample.INSTANCE.play(Assets.Sounds.ROCKS);
	}

	@Override public void unseal() {
		if (!locked) return;
		super.unseal();
		set(ARENA_DOOR, Terrain.EMPTY_DECO);
		set(entrance(), Terrain.ENTRANCE);
		set(EXIT, Terrain.EXIT);
		GameScene.updateMap(ARENA_DOOR);
		GameScene.updateMap(entrance());
		GameScene.updateMap(EXIT);
		Dungeon.observe();
	}

	Mob createLegacyBoss() {
		selectBoss();
		switch (bossVariant) {
			case LICH_DANCER: return new LichDancer();
			case ELDER_AVATAR: return new ElderAvatar();
			default: return new King();
		}
	}

	private void selectBoss() {
		if (bossVariant >= 0) return;
		if (Random.Int(3) == 1) bossVariant = LICH_DANCER;
		else if (Random.Int(2) == 1) bossVariant = ELDER_AVATAR;
		else bossVariant = KING;
	}

	int bossVariantForTesting() { selectBoss(); return bossVariant; }

	private boolean hasRequiredFightActor() {
		for (Mob mob : mobs) {
			if (bossVariant == LICH_DANCER && mob instanceof LichDancer) return true;
			if (bossVariant == ELDER_AVATAR && mob instanceof ElderAvatar) return true;
			if (bossVariant == KING && (mob instanceof King || mob instanceof King.DwarfKingTomb)) return true;
		}
		return false;
	}

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		selectBoss();
		bundle.put(ENTERED, enteredArena);
		bundle.put(BOSS_VARIANT, bossVariant);
	}

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		enteredArena = bundle.getBoolean(ENTERED);
		if (bundle.contains(BOSS_VARIANT)) bossVariant = bundle.getInt(BOSS_VARIANT);
		else {
			bossVariant = LICH_DANCER;
			for (Mob mob : mobs) {
				if (mob instanceof ElderAvatar || mob instanceof ElderAvatar.Obelisk) bossVariant = ELDER_AVATAR;
				else if (mob instanceof King || mob instanceof King.DwarfKingTomb || mob instanceof DwarfLich) bossVariant = KING;
			}
		}
		if (bossVariant < LICH_DANCER || bossVariant > KING) bossVariant = LICH_DANCER;
		if (enteredArena && locked && !hasRequiredFightActor()) spawnBoss(true);
	}
	@Override public String tilesTex() { return Assets.Environment.TILES_CITY; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_CITY; }
	@Override public void playLevelMusic() { Music.INSTANCE.play(Assets.Music.SPS_GAME, true); }

	@Override
	public Group addVisuals() {
		super.addVisuals();
		CityLevel.addCityVisuals(this, visuals);
		return visuals;
	}
}
