/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.SpsSokobanSheep;
import pd.actors.mobs.npcs.SheepSokoban;
import pd.actors.mobs.npcs.SheepSokobanBlack;
import pd.actors.mobs.npcs.SheepSokobanCorner;
import pd.actors.mobs.npcs.SheepSokobanStop;
import pd.actors.mobs.npcs.SheepSokobanSwitch;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.Towel;
import pd.items.keys.IronKey;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.levels.traps.FleecingTrap;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.journal.Notes;
import pd.levels.features.LevelTransition;
import pd.scenes.GameScene;
import pd.tiles.CustomTilemap;
import pd.tiles.custom.SpsLegacyLevelVisual;
import pd.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** Original 48x48 first SPS Sokoban journal map and its sheep mechanisms. */
public class SokobanIntroLevel extends Level implements SpsSokobanLevel {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 7 + WIDTH * 3;
	public static final int PORTAL = 7 + WIDTH * 21;
	public static final int PORTAL_SWITCH = 8 + WIDTH * 27;
	public static final int PORTAL_DESTINATION = 34 + WIDTH * 8;
	public static final int KEY_PRIZE_CELL = 24 + WIDTH * 21;
	public static final int TOWEL_PRIZE_CELL = 37 + WIDTH * 21;

	private int portalDestination;
	private int prizeNo;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_PRISON;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_PRISON;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		map = SokobanLayouts.SOKOBAN_INTRO_LEVEL.clone();
		if (map.length != length()) return false;
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_PUZZLE, width(), height(), map));
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		map[ENTRANCE] = Terrain.ENTRANCE;
		portalDestination = 0;
		prizeNo = 0;
		return true;
	}

	@Override
	protected void createMobs() {
		for (int cell = 0; cell < length(); cell++) {
			SpsSokobanSheep sheep = sheepForTerrain(map[cell]);
			if (sheep != null) {
				sheep.pos = cell;
				mobs.add(sheep);
			}
		}
	}

	private SpsSokobanSheep sheepForTerrain(int terrain) {
		switch (terrain) {
			case Terrain.SOKOBAN_SHEEP: return new SheepSokoban();
			case Terrain.CORNER_SOKOBAN_SHEEP: return new SheepSokobanCorner();
			case Terrain.SWITCH_SOKOBAN_SHEEP: return new SheepSokobanSwitch();
			case Terrain.BLACK_SOKOBAN_SHEEP: return new SheepSokobanBlack();
			default: return null;
		}
	}

	@Override
	protected void createItems() {
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.SOKOBAN_HEAP) {
				Item item = Random.Int(5) == 0
						? new ScrollOfUpgrade() : new Gold(Random.Int(300, 500));
				drop(item, cell).type = Heap.Type.CHEST;
			}
		}
	}

	public void afterSheepMoved(SpsSokobanSheep sheep) {
		int cell = sheep.pos;
		switch (map[cell]) {
			case Terrain.FLEECING_TRAP:
				sheep.destroy();
				if (sheep.sprite != null) sheep.sprite.killAndErase();
				setSpsTerrain(cell, Terrain.WOOL_RUG);
				break;
			case Terrain.CHANGE_SHEEP_TRAP:
				transformSheep(sheep, cell);
				setSpsTerrain(cell, Terrain.INACTIVE_TRAP);
				break;
			case Terrain.SOKOBAN_ITEM_REVEAL:
				Heap prize = drop(nextPrize(), prizeNo == 0 ? KEY_PRIZE_CELL : TOWEL_PRIZE_CELL);
				if (prize.sprite != null) prize.sprite.drop();
				prizeNo++;
				setSpsTerrain(cell, Terrain.EMPTY);
				if (prizeNo >= 2) {
					pd.items.quest.AdventureJournal.complete(1);
				}
				break;
			case Terrain.SOKOBAN_PORT_SWITCH:
				portalDestination = PORTAL_DESTINATION;
				setSpsTerrain(cell, Terrain.EMPTY);
				GLog.i("Click!");
				break;
		}
	}

	private Item nextPrize() {
		return prizeNo == 0 ? new IronKey(Dungeon.depth) : new Towel();
	}

	private void transformSheep(SpsSokobanSheep sheep, int cell) {
		SpsSokobanSheep replacement;
		if (sheep instanceof SpsSokobanSheep.Corner) replacement = new SheepSokobanStop();
		else if (sheep instanceof SpsSokobanSheep.Switch) replacement = new SheepSokoban();
		else replacement = new SheepSokobanCorner();
		sheep.destroy();
		if (sheep.sprite != null) sheep.sprite.killAndErase();
		replacement.pos = cell;
		GameScene.add(replacement, 0.2f);
	}

	private void setSpsTerrain(int cell, int terrain) {
		Level.set(cell, terrain);
		for (CustomTilemap visual : customTiles) {
			if (visual instanceof SpsLegacyLevelVisual) {
				((SpsLegacyLevelVisual) visual).updateTerrainCell(cell, terrain);
			}
		}
		GameScene.updateMap(cell);
	}

	@Override
	public void occupyCell(Char ch) {
		super.occupyCell(ch);
		if (ch == Dungeon.hero) {
			if (map[ch.pos] == Terrain.PORT_WELL && portalDestination > 0) {
				ScrollOfTeleportation.teleportToLocation(ch, portalDestination);
			} else if (map[ch.pos] == Terrain.FLEECING_TRAP) {
				Hero hero = (Hero) ch;
				if (FleecingTrap.destroyArmor(hero)) {
					GLog.n(pd.messages.Messages.get(this, "armor_destroyed"));
					setSpsTerrain(ch.pos, Terrain.INACTIVE_TRAP);
				} else {
					resetPuzzle(hero);
				}
			}
		}
	}

	public void resetPuzzle(Hero hero) {
		for (Mob mob : mobs.toArray(new Mob[0])) {
			if (mob instanceof SpsSokobanSheep) {
				mob.destroy();
				if (mob.sprite != null) mob.sprite.killAndErase();
				mobs.remove(mob);
			}
		}
		map = SokobanLayouts.SOKOBAN_INTRO_LEVEL.clone();
		map[ENTRANCE] = Terrain.ENTRANCE;
		portalDestination = 0;
		prizeNo = 0;
		removePrizeHeap(KEY_PRIZE_CELL);
		removePrizeHeap(TOWEL_PRIZE_CELL);
		buildFlagMaps();
		for (CustomTilemap visual : customTiles) {
			if (visual instanceof SpsLegacyLevelVisual) {
				((SpsLegacyLevelVisual) visual).resetTerrainMap(SokobanLayouts.SOKOBAN_INTRO_LEVEL);
			}
		}
		IronKey key = new IronKey(Dungeon.depth);
		while (Notes.remove(key)) {
		}
		createActiveSheep();
		ScrollOfTeleportation.teleportToLocation(hero, ENTRANCE);
		GameScene.updateMap();
	}

	private void removePrizeHeap(int cell) {
		Heap heap = heaps.get(cell);
		if (heap != null) {
			heaps.remove(cell);
			GameScene.discard(heap);
		}
	}

	private void createActiveSheep() {
		for (int cell = 0; cell < length(); cell++) {
			SpsSokobanSheep sheep = sheepForTerrain(map[cell]);
			if (sheep != null) {
				sheep.pos = cell;
				mobs.add(sheep);
				GameScene.add(sheep);
			}
		}
	}

	public int randomFleecingCell(int start, int maxDistance) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.FLEECING_TRAP && Actor.findChar(cell) == null
					&& distance(start, cell) < maxDistance) candidates.add(cell);
		}
		return candidates.isEmpty() ? -1 : Random.element(candidates);
	}

	@Override
	public Mob createMob() {
		return null;
	}

	@Override
	public Actor addRespawner() {
		return null;
	}

	@Override
	public int randomRespawnCell(Char ch) {
		return -1;
	}

	private static final String PORTAL_DEST = "portal_destination";
	private static final String PRIZE_NO = "prize_no";
	private static final String LEGACY_HEAPS_TO_GEN = "heapstogen";
	private static final String LEGACY_HEAP_GEN_SPOTS = "heapgenspots";
	private static final String LEGACY_TELEPORT_SPOTS = "teleportspots";
	private static final String LEGACY_PORT_SWITCH_SPOTS = "portswitchspots";
	private static final String LEGACY_DESTINATION_SPOTS = "destinationspots";
	private static final String LEGACY_TELEPORT_ASSIGN = "teleportassign";
	private static final String LEGACY_DESTINATION_ASSIGN = "destinationassign";
	private static final String LEGACY_PRIZE_NO = "prizeNo";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PORTAL_DEST, portalDestination);
		bundle.put(PRIZE_NO, prizeNo);

		bundle.put(LEGACY_HEAP_GEN_SPOTS, new int[]{KEY_PRIZE_CELL, TOWEL_PRIZE_CELL});
		bundle.put(LEGACY_TELEPORT_SPOTS, new int[]{PORTAL});
		bundle.put(LEGACY_PORT_SWITCH_SPOTS, new int[]{PORTAL_SWITCH});
		bundle.put(LEGACY_DESTINATION_SPOTS, new int[]{portalDestination});
		bundle.put(LEGACY_TELEPORT_ASSIGN, new int[]{PORTAL});
		bundle.put(LEGACY_DESTINATION_ASSIGN, new int[]{PORTAL_DESTINATION});
		bundle.put(LEGACY_PRIZE_NO, prizeNo);
		bundle.put(LEGACY_HEAPS_TO_GEN, legacyRemainingPrizes());
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(PORTAL_DEST)) {
			portalDestination = bundle.getInt(PORTAL_DEST);
		} else {
			int[] legacyDestinations = bundle.getIntArray(LEGACY_DESTINATION_SPOTS);
			portalDestination = legacyDestinations.length == 0 ? 0 : legacyDestinations[0];
		}
		prizeNo = bundle.contains(PRIZE_NO)
				? bundle.getInt(PRIZE_NO) : bundle.getInt(LEGACY_PRIZE_NO);
		prizeNo = Math.max(0, Math.min(2, prizeNo));
	}

	private ArrayList<Item> legacyRemainingPrizes() {
		ArrayList<Item> remaining = new ArrayList<>();
		if (prizeNo <= 0) remaining.add(new IronKey(Dungeon.depth));
		if (prizeNo <= 1) remaining.add(new Towel());
		return remaining;
	}
}
