/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.GnollKing;
import pd.actors.mobs.Mob;
import pd.items.TreasureMap;
import pd.items.potions.PotionOfLiquidFlame;
import pd.items.quest.AdventureJournal;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import pd.scenes.GameScene;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.Arrays;

/** Original 48-wide treasure field used by the Gnoll King quest. */
public class FieldBossLevel extends Level {
	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int TOP = 2;
	public static final int HALL_WIDTH = 13;
	public static final int HALL_HEIGHT = 15;
	public static final int CHAMBER_HEIGHT = 3;
	public static final int LEFT = (WIDTH - HALL_WIDTH) / 2;
	public static final int CENTER = LEFT + HALL_WIDTH / 2;

	private int arenaDoor;
	private int entranceCell;
	private boolean enteredArena;

	{
		color1 = 0x48763c;
		color2 = 0x59994a;
		viewDistance = 8;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		Arrays.fill(map, Terrain.WALL);
		Painter.fill(this, LEFT, TOP, HALL_WIDTH, HALL_HEIGHT, Terrain.EMPTY);
		for (int y = TOP + 1; y < TOP + HALL_HEIGHT; y += 2) {
			map[y * width() + CENTER - 2] = Terrain.WALL;
			map[y * width() + CENTER + 2] = Terrain.WALL;
		}

		arenaDoor = (TOP + HALL_HEIGHT) * width() + CENTER;
		map[arenaDoor] = Terrain.DOOR;
		Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, CHAMBER_HEIGHT, Terrain.EMPTY);
		Painter.fill(this, LEFT, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.WATER);
		Painter.fill(this, LEFT + HALL_WIDTH - 1, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.WATER);

		entranceCell = (TOP + HALL_HEIGHT + 2 + Random.Int(CHAMBER_HEIGHT - 1)) * width()
				+ LEFT + Random.Int(HALL_WIDTH - 2);
		map[entranceCell] = Terrain.PEDESTAL;
		transitions.add(new LevelTransition(this, entranceCell,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));

		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.EMPTY && Random.Float() < 0.20f) map[cell] = Terrain.HIGH_GRASS;
			else if (map[cell] == Terrain.EMPTY && Random.Float() < 0.25f) map[cell] = Terrain.GRASS;
			else if (map[cell] == Terrain.EMPTY && Random.Float() < 0.30f) map[cell] = Terrain.EMPTY_DECO;
			else if (map[cell] == Terrain.WALL && Random.Int(8) == 0) map[cell] = Terrain.WALL_DECO;
		}
		map[arenaDoor + width()] = Terrain.HIGH_GRASS;
		map[arenaDoor + width() + 1] = Terrain.HIGH_GRASS;
		map[arenaDoor + width() - 1] = Terrain.HIGH_GRASS;
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_FOREST, width(), height(), map));
		locked = !bossDefeated();
		return true;
	}

	@Override protected void createMobs() { }
	@Override protected void createItems() {
		drop(new PotionOfLiquidFlame(), arenaDoor + 2 * width());
	}
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return safeSpawnCell(entranceCell); }

	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		// The map itself is the return key. Walking over the pedestal must never
		// bypass its boss check or leave an unconsumed map in the inventory.
		return false;
	}

	@Override
	public void pressCell(int cell) {
		super.pressCell(cell);
		if (!enteredArena && !bossDefeated() && Dungeon.hero != null
				&& Dungeon.hero.pos == cell && cell / width() < arenaDoor / width()) {
			enteredArena = true;
			spawnKing();
			Dungeon.observe();
		}
	}

	private void spawnKing() {
		GnollKing king = new GnollKing();
		king.pos = safeBossSpawnCell();
		king.state = king.HUNTING;
		GameScene.add(king);
		king.notice();
	}

	private int safeBossSpawnCell() {
		int visibleCandidates = 0;
		for (int tries = 0; tries < 1000; tries++) {
			int cell = Random.Int(length());
			if (!passable[cell] || Actor.findChar(cell) != null
					|| cell / width() >= arenaDoor / width()) continue;
			boolean visible = heroFOV != null && cell < heroFOV.length && heroFOV[cell];
			if (!visible || visibleCandidates++ >= 20) return cell;
		}
		for (int cell = 0; cell < length(); cell++) {
			if (passable[cell] && Actor.findChar(cell) == null
					&& cell / width() < arenaDoor / width()
					&& (heroFOV == null || cell >= heroFOV.length || !heroFOV[cell])) return cell;
		}
		return safeSpawnCell(entranceCell);
	}

	public int safeSpawnCell(int preferred) {
		if (insideMap(preferred) && passable[preferred] && Actor.findChar(preferred) == null
				&& preferred / width() < arenaDoor / width()) return preferred;
		for (int tries = 0; tries < 200; tries++) {
			int cell = Random.Int(length());
			if (passable[cell] && Actor.findChar(cell) == null && cell / width() < arenaDoor / width()) return cell;
		}
		for (int cell = 0; cell < length(); cell++) {
			if (passable[cell] && Actor.findChar(cell) == null && cell / width() < arenaDoor / width()) return cell;
		}
		return entranceCell;
	}

	public void kingDefeated() {
		locked = false;
		Dungeon.gnollKingKilled = true;
		if (isAdventureRoute()) AdventureJournal.complete(14);
		GameScene.bossSlain();
	}

	private boolean isAdventureRoute() {
		return Dungeon.branch == AdventureJournal.branchFor(14);
	}

	private boolean bossDefeated() {
		if (!isAdventureRoute()) return Dungeon.gnollKingKilled;
		if (Dungeon.hero == null) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		return journal != null && journal.isCompleted(14);
	}

	@Override public String tilesTex() { return Assets.Environment.TILES_CAVES; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_SEWERS; }

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
		locked = !bossDefeated();
		if (enteredArena && !bossDefeated()) {
			boolean found = false;
			for (Mob mob : mobs()) if (mob instanceof GnollKing) { found = true; break; }
			if (!found) {
				GnollKing king = new GnollKing();
				king.pos = safeSpawnCell(entranceCell - 5 * width());
				king.state = king.HUNTING;
				mobs().add(king);
			}
		}
	}
}
