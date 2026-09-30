/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SokobanSentinel;
import pd.actors.mobs.npcs.SpsSokobanSheep;
import pd.actors.mobs.npcs.SheepSokoban;
import pd.actors.mobs.npcs.SheepSokobanBlack;
import pd.actors.mobs.npcs.SheepSokobanCorner;
import pd.actors.mobs.npcs.SheepSokobanStop;
import pd.actors.mobs.npcs.SheepSokobanSwitch;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.keys.IronKey;
import pd.items.misc.Spectacles;
import pd.items.potions.PotionOfLiquidFlame;
import pd.items.quest.AdventureJournal;
import pd.items.scrolls.ScrollOfMagicalInfusion;
import pd.items.scrolls.ScrollOfRegrowth;
import pd.items.scrolls.ScrollOfTeleportation;
import pd.levels.traps.FleecingTrap;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.journal.Notes;
import pd.levels.features.LevelTransition;
import pd.plants.ReNepenth;
import pd.plants.Starflower;
import pd.scenes.GameScene;
import pd.tiles.CustomTilemap;
import pd.tiles.custom.SpsLegacyLevelVisual;
import pd.utils.GLog;
import watabou.utils.Bundle;
import watabou.utils.Bundlable;
import watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;

/** Original 48x48 SPS Sokoban castle, journal destination 2. */
public class SokobanCastle extends Level implements SpsSokobanLevel {
	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 24 + WIDTH * 22;
	public static final int SENTINEL_CELL = 38 + WIDTH * 20;
	public static final int PORTAL_SWITCH = 32 + WIDTH * 40;
	public static final int[] PORTALS = {4 + WIDTH * 3, 7 + WIDTH * 27, 37 + WIDTH * 35};
	public static final int[] INITIAL_PORTAL_DESTINATIONS = {ENTRANCE, 1 + WIDTH * 27, 0};
	public static final int SWITCH_DESTINATION = 9 + WIDTH * 37;
	public static final int[] PORTAL_SWITCHES = {PORTAL_SWITCH};
	public static final int[] PORTAL_SWITCH_PORTALS = {PORTALS[2]};
	public static final int[] PORTAL_SWITCH_DESTINATIONS = {SWITCH_DESTINATION};
	public static final int[] SENTINEL_CELLS = {SENTINEL_CELL};
	public static final int[] PRIZE_CELLS = {
			ENTRANCE, ENTRANCE, ENTRANCE, ENTRANCE, ENTRANCE, ENTRANCE,
			30 + WIDTH * 23, 18 + WIDTH * 23, 25 + WIDTH * 2,
			26 + WIDTH * 2, 8 + WIDTH * 37, 10 + WIDTH * 37
	};

	private int[] portalDestinations;
	private int prizeNo;
	private boolean bonusPrizes;
	private ArrayList<Integer> remainingNonKeyPrizes;

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
		map = layout().clone();
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_PUZZLE, width(), height(), map));
		transitions.add(new LevelTransition(this, entranceCell(),
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		map[entranceCell()] = Terrain.ENTRANCE;
		portalDestinations = initialPortalDestinations().clone();
		prizeNo = 0;
		AdventureJournal journal = Dungeon.hero == null ? null
				: Dungeon.hero.belongings.getItem(AdventureJournal.class);
		bonusPrizes = journal == null || !journal.isCompleted(destinationIndex());
		resetPrizePool();
		return true;
	}

	protected int[] layout() { return SokobanLayouts.SOKOBAN_CASTLE; }
	protected int entranceCell() { return ENTRANCE; }
	protected int destinationIndex() { return 2; }
	protected int[] portalCells() { return PORTALS; }
	protected int[] initialPortalDestinations() { return INITIAL_PORTAL_DESTINATIONS; }
	protected int[] portalSwitchCells() { return PORTAL_SWITCHES; }
	protected int[] portalSwitchPortals() { return PORTAL_SWITCH_PORTALS; }
	protected int[] portalSwitchDestinations() { return PORTAL_SWITCH_DESTINATIONS; }
	protected int[] sentinelCells() { return SENTINEL_CELLS; }
	protected int[] prizeCells() { return PRIZE_CELLS; }
	protected boolean hasBonusPrizes() { return bonusPrizes; }
	protected int nonKeyPrizeCount() { return 6; }

	private void resetPrizePool() {
		remainingNonKeyPrizes = new ArrayList<>();
		remainingNonKeyPrizes.add(0);
		if (bonusPrizes) {
			for (int i = 1; i < nonKeyPrizeCount(); i++) remainingNonKeyPrizes.add(i);
		}
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
		for (int cell : sentinelCells()) {
			SokobanSentinel sentinel = new SokobanSentinel();
			sentinel.pos = cell;
			mobs.add(sentinel);
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
				Item item = hasBonusPrizes() && Random.Int(5) == 0
						? new ScrollOfUpgrade() : new Gold(Random.Int(goldMinimum(), goldMaximum()));
				drop(item, cell).type = Heap.Type.CHEST;
			}
		}
		createFixedItems();
	}

	protected int goldMinimum() { return hasBonusPrizes() ? 400 : 300; }
	protected int goldMaximum() { return hasBonusPrizes() ? 800 : 500; }
	protected void createFixedItems() {
		drop(new PotionOfLiquidFlame(), 9 + WIDTH * 24).type = Heap.Type.CHEST;
	}

	@Override
	public void afterSheepMoved(SpsSokobanSheep sheep) {
		int cell = sheep.pos;
		switch (map[cell]) {
			case Terrain.FLEECING_TRAP:
				destroySheep(sheep);
				setSpsTerrain(cell, Terrain.WOOL_RUG);
				break;
			case Terrain.CHANGE_SHEEP_TRAP:
				if (transformSheep(sheep, cell)) {
					setSpsTerrain(cell, Terrain.INACTIVE_TRAP);
				}
				break;
			case Terrain.SOKOBAN_ITEM_REVEAL:
				Item item = prizeFor(prizeNo);
				if (item != null) {
					int[] prizeCells = prizeCells();
					Heap prize = drop(item, prizeCells[Math.min(prizeNo, prizeCells.length - 1)]);
					if (prize.sprite != null) prize.sprite.drop();
				}
				prizeNo++;
				setSpsTerrain(cell, Terrain.EMPTY);
				if (prizeNo >= prizeCells().length) AdventureJournal.complete(destinationIndex());
				break;
			case Terrain.SOKOBAN_PORT_SWITCH:
				activatePortalSwitch(cell);
				setSpsTerrain(cell, Terrain.EMPTY);
				GLog.i("Click!");
				break;
		}
	}

	protected Item prizeFor(int index) {
		if (index < 6) return new IronKey(Dungeon.depth);
		if (remainingNonKeyPrizes.isEmpty()) return null;
		return nonKeyPrize(remainingNonKeyPrizes.remove(Random.Int(remainingNonKeyPrizes.size())));
	}

	protected Item nonKeyPrize(int id) {
		switch (id) {
			case 0: return new Spectacles();
			case 1: return new Starflower.Seed();
			case 2: return new ReNepenth.Seed();
			case 3:
			case 4: return new ScrollOfMagicalInfusion();
			case 5: return new ScrollOfRegrowth();
			default: return null;
		}
	}

	private void activatePortalSwitch(int cell) {
		int[] switches = portalSwitchCells();
		for (int i = 0; i < switches.length; i++) {
			if (switches[i] != cell) continue;
			int portalCell = portalSwitchPortals()[i];
			int[] portals = portalCells();
			for (int portal = 0; portal < portals.length; portal++) {
				if (portals[portal] == portalCell) {
					portalDestinations[portal] = portalSwitchDestinations()[i];
					return;
				}
			}
		}
	}

	private boolean transformSheep(SpsSokobanSheep sheep, int cell) {
		SpsSokobanSheep replacement;
		if (sheep instanceof SpsSokobanSheep.Corner) replacement = new SheepSokobanStop();
		else if (sheep instanceof SpsSokobanSheep.Switch) replacement = new SheepSokoban();
		else if (sheep instanceof SpsSokobanSheep.Black
				|| sheep instanceof SpsSokobanSheep.Stop) return false;
		else replacement = new SheepSokobanCorner();
		destroySheep(sheep);
		replacement.pos = cell;
		GameScene.add(replacement, 0.2f);
		return true;
	}

	private void destroySheep(SpsSokobanSheep sheep) {
		sheep.destroy();
		if (sheep.sprite != null) sheep.sprite.killAndErase();
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
		if (ch != Dungeon.hero) return;
		int[] portals = portalCells();
		for (int i = 0; i < portals.length; i++) {
			if (ch.pos == portals[i] && portalDestinations[i] > 0) {
				ScrollOfTeleportation.teleportToLocation(ch, portalDestinations[i]);
				return;
			}
		}
		if (map[ch.pos] == Terrain.FLEECING_TRAP) {
			Hero hero = (Hero) ch;
			if (FleecingTrap.destroyArmor(hero)) {
				GLog.n(pd.messages.Messages.get(this, "armor_destroyed"));
				setSpsTerrain(ch.pos, Terrain.INACTIVE_TRAP);
			} else {
				resetPuzzle(hero);
			}
		}
	}

	public void resetPuzzle(Hero hero) {
		for (Mob mob : mobs.toArray(new Mob[0])) {
			if (mob instanceof SpsSokobanSheep || mob instanceof SokobanSentinel) {
				mob.destroy();
				if (mob.sprite != null) mob.sprite.killAndErase();
			}
		}
		map = layout().clone();
		map[entranceCell()] = Terrain.ENTRANCE;
		portalDestinations = initialPortalDestinations().clone();
		prizeNo = 0;
		bonusPrizes = false;
		resetPrizePool();
		for (int cell : prizeCells()) removePrizeHeap(cell);
		buildFlagMaps();
		for (CustomTilemap visual : customTiles) {
			if (visual instanceof SpsLegacyLevelVisual) {
				((SpsLegacyLevelVisual) visual).resetTerrainMap(layout());
			}
		}
		IronKey key = new IronKey(Dungeon.depth);
		while (Notes.remove(key)) {
		}
		createActiveMobs();
		ScrollOfTeleportation.teleportToLocation(hero, entranceCell());
		GameScene.updateMap();
	}

	private void removePrizeHeap(int cell) {
		Heap heap = heaps.get(cell);
		if (heap != null) {
			heaps.remove(cell);
			GameScene.discard(heap);
		}
	}

	private void createActiveMobs() {
		for (int cell = 0; cell < length(); cell++) {
			SpsSokobanSheep sheep = sheepForTerrain(map[cell]);
			if (sheep != null) {
				sheep.pos = cell;
				GameScene.add(sheep);
			}
		}
		for (int cell : sentinelCells()) {
			SokobanSentinel sentinel = new SokobanSentinel();
			sentinel.pos = cell;
			GameScene.add(sentinel);
		}
	}

	@Override
	public int randomFleecingCell(int start, int maxDistance) {
		boolean hasNearTrap = false;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.FLEECING_TRAP && Actor.findChar(cell) == null
					&& distance(start, cell) <= maxDistance) {
				candidates.add(cell);
				if (distance(start, cell) < maxDistance) hasNearTrap = true;
			}
		}
		return !hasNearTrap || candidates.isEmpty() ? -1 : Random.element(candidates);
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

	private static final String PORTAL_DESTINATIONS = "portal_destinations";
	private static final String PRIZE_NO = "prize_no";
	private static final String BONUS_PRIZES = "bonus_prizes";
	private static final String REMAINING_PRIZES = "remaining_prizes";
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
		bundle.put(PORTAL_DESTINATIONS, portalDestinations);
		bundle.put(PRIZE_NO, prizeNo);
		bundle.put(BONUS_PRIZES, bonusPrizes);
		int[] remaining = new int[remainingNonKeyPrizes.size()];
		for (int i = 0; i < remaining.length; i++) remaining[i] = remainingNonKeyPrizes.get(i);
		bundle.put(REMAINING_PRIZES, remaining);

		bundle.put(LEGACY_TELEPORT_SPOTS, portalCells());
		bundle.put(LEGACY_PORT_SWITCH_SPOTS, portalSwitchCells());
		bundle.put(LEGACY_DESTINATION_SPOTS, portalDestinations);
		bundle.put(LEGACY_TELEPORT_ASSIGN, portalSwitchPortals());
		bundle.put(LEGACY_DESTINATION_ASSIGN, portalSwitchDestinations());
		bundle.put(LEGACY_HEAP_GEN_SPOTS, prizeCells());
		bundle.put(LEGACY_PRIZE_NO, prizeNo);
		bundle.put(LEGACY_HEAPS_TO_GEN, legacyRemainingPrizes());
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		int[] savedDestinations = bundle.contains(PORTAL_DESTINATIONS)
				? bundle.getIntArray(PORTAL_DESTINATIONS)
				: bundle.getIntArray(LEGACY_DESTINATION_SPOTS);
		portalDestinations = normalizePortalDestinations(savedDestinations);
		prizeNo = bundle.contains(PRIZE_NO) ? bundle.getInt(PRIZE_NO)
				: bundle.getInt(LEGACY_PRIZE_NO);

		if (bundle.contains(REMAINING_PRIZES)) {
			bonusPrizes = bundle.getBoolean(BONUS_PRIZES);
			remainingNonKeyPrizes = new ArrayList<>();
			for (int value : bundle.getIntArray(REMAINING_PRIZES)) {
				if (value >= 0 && value < nonKeyPrizeCount()) remainingNonKeyPrizes.add(value);
			}
		} else if (bundle.contains(LEGACY_HEAPS_TO_GEN)) {
			restoreLegacyPrizePool(bundle);
		} else {
			bonusPrizes = bundle.getBoolean(BONUS_PRIZES);
			resetPrizePool();
			for (int i = 0; i < Math.max(0, prizeNo - 6) && !remainingNonKeyPrizes.isEmpty(); i++) {
				remainingNonKeyPrizes.remove(0);
			}
		}
	}

	private int[] normalizePortalDestinations(int[] saved) {
		int count = portalCells().length;
		if (saved == null || saved.length < count) return initialPortalDestinations().clone();
		int[] result = new int[count];
		System.arraycopy(saved, 0, result, 0, count);
		return result;
	}

	private Collection<Item> legacyRemainingPrizes() {
		ArrayList<Item> items = new ArrayList<>();
		for (int i = prizeNo; i < 6; i++) items.add(new IronKey(Dungeon.depth));
		for (int id : remainingNonKeyPrizes) {
			Item item = nonKeyPrize(id);
			if (item != null) items.add(item);
		}
		return items;
	}

	private void restoreLegacyPrizePool(Bundle bundle) {
		remainingNonKeyPrizes = new ArrayList<>();
		boolean[] restored = new boolean[nonKeyPrizeCount()];
		Collection<Bundlable> savedItems = bundle.getCollection(LEGACY_HEAPS_TO_GEN);
		for (Bundlable saved : savedItems) {
			if (!(saved instanceof Item) || saved instanceof IronKey) continue;
			for (int id = 0; id < restored.length; id++) {
				Item candidate = nonKeyPrize(id);
				if (!restored[id] && candidate != null && candidate.getClass() == saved.getClass()) {
					restored[id] = true;
					remainingNonKeyPrizes.add(id);
					break;
				}
			}
		}
		bonusPrizes = prizeNo > 7;
		for (int id : remainingNonKeyPrizes) {
			if (id > 0) {
				bonusPrizes = true;
				break;
			}
		}
	}
}
