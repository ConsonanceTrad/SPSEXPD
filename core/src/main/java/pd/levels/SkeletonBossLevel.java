/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SkeletonHand1;
import pd.actors.mobs.SkeletonHand2;
import pd.actors.mobs.SkeletonKing;
import pd.items.potions.PotionOfLiquidFlame;
import pd.items.quest.AdventureJournal;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import pd.scenes.GameScene;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class SkeletonBossLevel extends Level {
	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int TOP = 2;
	public static final int HALL_WIDTH = 13;
	public static final int HALL_HEIGHT = 15;
	public static final int CHAMBER_HEIGHT = 3;
	public static final int LEFT = (WIDTH - HALL_WIDTH) / 2;
	public static final int CENTER = LEFT + HALL_WIDTH / 2;
	public static final int HAND_1_CELL = CENTER + (TOP + 1) * WIDTH;
	public static final int HAND_2_CELL = HAND_1_CELL + 1;

	private int arenaDoor;
	private boolean enteredArena;

	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
		viewDistance = 8;
	}

	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_SKELETON; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_PRISON; }

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		Painter.fill(this, LEFT, TOP, HALL_WIDTH, HALL_HEIGHT, Terrain.EMPTY);
		Painter.fill(this, CENTER, TOP, 1, HALL_HEIGHT, Terrain.EMPTY);
		for (int y = TOP + 1; y < TOP + HALL_HEIGHT; y += 2) {
			map[y * WIDTH + CENTER - 2] = Terrain.STATUE;
			map[y * WIDTH + CENTER + 2] = Terrain.STATUE;
		}
		int legacyExit = (TOP - 1) * WIDTH + CENTER;
		map[legacyExit] = Terrain.WALL;
		arenaDoor = (TOP + HALL_HEIGHT) * WIDTH + CENTER;
		map[arenaDoor] = Terrain.DOOR;
		Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, CHAMBER_HEIGHT, Terrain.EMPTY);
		Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.WATER);
		Painter.fill(this, LEFT + HALL_WIDTH - 1, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.WATER);
		int entrance = (TOP + HALL_HEIGHT + 2 + Random.Int(CHAMBER_HEIGHT - 1)) * WIDTH
				+ LEFT + Random.Int(HALL_WIDTH - 2);
		map[entrance] = Terrain.PEDESTAL;
		transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		decorateLegacy();
		return true;
	}

	private void decorateLegacy() {
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Int(10) == 0) map[cell] = Terrain.EMPTY_DECO;
			else if (map[cell] == Terrain.WALL && Random.Int(8) == 0) map[cell] = Terrain.WALL_DECO;
		}
		int shrub1 = arenaDoor + WIDTH;
		map[shrub1] = Terrain.SHRUB;
		map[shrub1 - 1] = Terrain.SHRUB;
		map[shrub1 + 1] = Terrain.SHRUB;
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.WALL && Random.Int(8) == 0) map[cell] = Terrain.WALL_DECO;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.20f) map[cell] = Terrain.HIGH_GRASS;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.25f) map[cell] = Terrain.GRASS;
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.30f) map[cell] = Terrain.SHRUB;
		}
	}

	@Override protected void createMobs() { }
	@Override protected void createItems() { drop(new PotionOfLiquidFlame(), arenaDoor + 2 * WIDTH); }
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }

	@Override
	public void pressCell(int cell) {
		super.pressCell(cell);
		if (!enteredArena && Dungeon.hero != null && Dungeon.hero.pos == cell && outsideEntranceRoom(cell)) {
			enteredArena = true;
			spawnBosses();
			Dungeon.observe();
		}
	}

	private void spawnBosses() {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < length(); cell++) {
			if (passable[cell] && outsideEntranceRoom(cell) && Actor.findChar(cell) == null
					&& cell != HAND_1_CELL && cell != HAND_2_CELL) candidates.add(cell);
		}
		if (candidates.isEmpty()) return;
		SkeletonKing king = new SkeletonKing();
		king.pos = Random.element(candidates);
		king.state = king.HUNTING;
		SkeletonHand1 hand1 = new SkeletonHand1();
		hand1.pos = HAND_1_CELL;
		hand1.state = hand1.HUNTING;
		SkeletonHand2 hand2 = new SkeletonHand2();
		hand2.pos = HAND_2_CELL;
		hand2.state = hand2.HUNTING;
		GameScene.add(king);
		GameScene.add(hand1);
		GameScene.add(hand2);
	}

	boolean outsideEntranceRoom(int cell) { return cell / WIDTH < arenaDoor / WIDTH; }
	int arenaDoorForTesting() { return arenaDoor; }

	private boolean completed() {
		if (Dungeon.hero == null) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		return journal != null && journal.isCompleted(11);
	}

	private static final String DOOR = "door";
	private static final String ENTERED = "entered";
	@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(DOOR, arenaDoor); b.put(ENTERED, enteredArena); }
	@Override public void restoreFromBundle(Bundle b) {
		super.restoreFromBundle(b); arenaDoor = b.getInt(DOOR); enteredArena = b.getBoolean(ENTERED);
		if (enteredArena && !completed()) {
			boolean found = false;
			for (Mob mob : mobs) if (mob instanceof SkeletonKing) { found = true; break; }
			if (!found) enteredArena = false;
		}
	}
}
