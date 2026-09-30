/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.misc.LuckyBadge;
import pd.items.quest.AdventureJournal;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.levels.features.LevelTransition;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.utils.Bundle;
import render.utils.Random;

/** Legacy grass treasure room. Its portal and sheep tables were empty in SPS-PD 0.9.8. */
public class RoomOfZotLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 23 + WIDTH * 15;
	private boolean firstVisit;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		map = SaveRoomLayouts.ROOM_OF_GRASS.clone();
		if (map.length != length()) return false;
		firstVisit = !completed();
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_PUZZLE, width(), height(), map));
		map[ENTRANCE] = Terrain.ENTRANCE;
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		return true;
	}

	@Override protected void createMobs() { }

	@Override
	protected void createItems() {
		int goldMin = firstVisit ? 50 : 1;
		int goldMax = firstVisit ? 100 : 10;
		boolean badgeDropped = false;
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] != Terrain.EMPTY || heaps.get(cell) != null || Random.Int(100) <= 70) continue;
			if (firstVisit && !badgeDropped) {
				drop(new LuckyBadge(), cell).type = Heap.Type.CHEST;
				badgeDropped = true;
			} else if (firstVisit && Random.Int(50) == 0) {
				drop(new ScrollOfUpgrade(), cell).type = Heap.Type.CHEST;
			} else {
				drop(new Gold(Random.Int(goldMin, goldMax)), cell).type = Heap.Type.CHEST;
			}
		}
	}

	@Override
	public void occupyCell(Char ch) {
		super.occupyCell(ch);
		if (ch instanceof Hero && AdventureJournal.destinationForBranch(Dungeon.branch) == 20) {
			AdventureJournal.complete(20);
		}
	}

	private boolean completed() {
		if (Dungeon.hero == null) return false;
		AdventureJournal journal = Dungeon.hero.belongings.getItem(AdventureJournal.class);
		return journal != null && journal.isCompleted(20);
	}

	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }
	@Override public String tilesTex() { return Assets.Environment.TILES_PRISON; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_PRISON; }

	public boolean firstVisitForTesting() { return firstVisit; }

	private static final String FIRST_VISIT = "first_visit";
	@Override public void storeInBundle(Bundle b) { super.storeInBundle(b); b.put(FIRST_VISIT, firstVisit); }
	@Override public void restoreFromBundle(Bundle b) { super.restoreFromBundle(b); firstVisit = b.getBoolean(FIRST_VISIT); }
}
