/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.actors.mobs.BanditKing;
import pd.items.quest.AdventureJournal;
import pd.levels.builders.SpsBspLayout;
import pd.levels.builders.SpsBspLayout.Room;
import pd.levels.builders.SpsBspLayout.Type;
import pd.levels.features.LevelTransition;
import pd.levels.painters.CavesPainter;
import pd.levels.painters.Painter;
import render.utils.Bundle;
import render.utils.Random;

import java.util.ArrayList;

/** Legacy depth-41 pursuit maze: four loop legs and one attached royal chamber. */
public class ThiefCatchLevel extends SpsRegularLevel {

	private static final int LAYOUT_ATTEMPTS = 64;
	private int returnCell;
	private int sealedEntrance;

	{
		color1 = 0x48763c;
		color2 = 0x59994a;
	}

	@Override public String tilesTex() { return Assets.Environment.SPS_TILES_CAVES_LEGACY; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_CAVES; }
	@Override protected Painter painter() { return new CavesPainter(); }

	@Override
	protected SpsBspLayout.Result generateLegacyLayout() {
		return SpsBspLayout.generateThiefCatch(LEGACY_WIDTH, LEGACY_HEIGHT, LAYOUT_ATTEMPTS);
	}

	@Override
	protected void assignLegacyRoomTypes() {
		// The dedicated generator assigns the entrance, loop, tunnels and royal chamber.
	}

	@Override
	protected void afterLegacyRoomsPainted() {
		Room room = legacyLayout.entrance;
		returnCell = (room.left + room.right) / 2 + room.top * width();
		exit = returnCell;
		map[returnCell] = Terrain.EMPTY_SP;
		transitions.clear();
		transitions.add(new LevelTransition(this, entrance,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
	}

	@Override
	protected void decorateLegacyFloor() {
		Room room = legacyLayout.entrance;
		int start = room.top * width() + room.left + 1;
		int end = start + room.width() - 1;
		for (int cell = start; cell < end; cell++) {
			if (cell != returnCell) {
				map[cell] = Terrain.WALL_DECO;
				map[cell + width()] = Terrain.WATER;
			} else {
				map[cell + width()] = Terrain.EMPTY;
			}
		}
	}

	@Override protected float legacyWaterFill() { return 0.50f; }
	@Override protected int legacyWaterClustering() { return 5; }
	@Override protected float legacyGrassFill() { return 0.40f; }
	@Override protected int legacyGrassClustering() { return 4; }
	@Override protected float legacyChasmFill() { return 0f; }
	@Override protected int nTraps() { return 0; }

	@Override
	protected void createMobs() {
		if (completed()) return;
		ArrayList<Room> candidates = new ArrayList<>();
		for (Room room : legacyLayout.rooms) if (room.type == Type.STANDARD) candidates.add(room);
		if (candidates.isEmpty()) return;
		Room room = Random.element(candidates);
		int cell = room.randomCell(width(), 0);
		if (cell < 0) return;
		BanditKing king = new BanditKing();
		king.pos = cell;
		mobs.add(king);
	}

	@Override protected void createItems() { }
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }

	private boolean completed() {
		if (Dungeon.hero == null) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		return journal != null && journal.isCompleted(18);
	}

	@Override
	public void seal() {
		if (sealedEntrance == 0) {
			sealedEntrance = entrance();
			locked = true;
			set(sealedEntrance, Terrain.WATER);
		}
	}

	@Override
	public void unseal() {
		locked = false;
		if (sealedEntrance != 0) {
			set(sealedEntrance, Terrain.ENTRANCE);
			sealedEntrance = 0;
		}
	}

	public int returnCellForTesting() { return returnCell; }
	public int standardRoomCountForTesting() {
		int count = 0;
		for (Room room : legacyLayout.rooms) if (room.type == Type.STANDARD) count++;
		return count;
	}
	public boolean entranceHasConnectedRoomAboveForTesting() {
		for (Room room : legacyLayout.entrance.neighbours) {
			if (room.bottom == legacyLayout.entrance.top && room.type != Type.NULL) return true;
		}
		return false;
	}

	private static final String RETURN_CELL = "return_cell";
	private static final String SEALED_ENTRANCE = "sealed_entrance";
	@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(RETURN_CELL, returnCell); b.put(SEALED_ENTRANCE, sealedEntrance); }
	@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); returnCell = b.getInt(RETURN_CELL); sealedEntrance = b.getInt(SEALED_ENTRANCE); }
}
