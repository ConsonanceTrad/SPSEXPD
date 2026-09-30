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
import pd.actors.mobs.Mob;
import pd.actors.mobs.TestMob;
import pd.actors.mobs.YearBeast2;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.features.LevelTransition;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** The original 48x48 Spring Festival town, journal destination 6. */
public class SpringFestivalLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 8 + WIDTH * 5;
	public static final int LEGACY_EXIT = 0;
	public static final int FESTIVAL_JEWELRY_STORE = 7 + WIDTH * 10;
	public static final int FESTIVAL_SUPPLY_STORE = 21 + WIDTH * 35;

	private static final String LEGACY_MINE_DEPTH = "mineDepth";
	private static final String LEGACY_STORE_SPOTS = "storespots";
	private static final String LEGACY_SUPPLY_SPOTS = "bombpots";

	private int mineDepth;
	private int[] jewelryStoreSpots = {FESTIVAL_JEWELRY_STORE};
	private int[] supplyStoreSpots = {FESTIVAL_SUPPLY_STORE};
	private boolean stockChecked;

	public static final TownNpc.Spec[] RESTORED_RESIDENTS = {
			TownNpc.Spec.G2159687, TownNpc.Spec.EVAN, TownNpc.Spec.HBB,
			TownNpc.Spec.SFB, TownNpc.Spec.JINKELOID, TownNpc.Spec.XIXI_ZERO,
			TownNpc.Spec.RUSTYBLADE, TownNpc.Spec.LYN, TownNpc.Spec.LERY,
			TownNpc.Spec.FRUIT_CAT, TownNpc.Spec.COCONUT2, TownNpc.Spec.HMDZL001,
			TownNpc.Spec.ASH_WOLF,
			TownNpc.Spec.OMICRONRG9, TownNpc.Spec.MILLILITRE, TownNpc.Spec.HONEY_POOOOT,
			TownNpc.Spec.UNCLE_S, TownNpc.Spec.A_REAL_MAN, TownNpc.Spec.SAID_BY_SUN,
			TownNpc.Spec.DREAM_PLAYER, TownNpc.Spec.ICE13, TownNpc.Spec.GOBLIN_PLAYER,
			TownNpc.Spec.A_FLY, TownNpc.Spec.TEMPEST102, TownNpc.Spec.MEMORY_OF_SAND,
			TownNpc.Spec.APOSTLE, TownNpc.Spec.SHOWER, TownNpc.Spec.HATE_SOKOBAN,
			TownNpc.Spec.LAJI, TownNpc.Spec.NEW_PLAYER, TownNpc.Spec.THANK_LIST,
			TownNpc.Spec.STORM_AND_RAIN, TownNpc.Spec.LYNN,
			TownNpc.Spec.RENNPC, TownNpc.Spec.KOSTIS12345, TownNpc.Spec.GEOLOGIST
	};
	public static final int[] RESTORED_RESIDENT_CELLS = {
			36 + WIDTH * 6, 40 + WIDTH * 42, 38 + WIDTH * 13,
			34 + WIDTH * 11, 38 + WIDTH * 41, 25 + WIDTH * 11,
			36 + WIDTH * 15, 37 + WIDTH * 9, 33 + WIDTH * 9,
			37 + WIDTH * 4, 20 + WIDTH * 35, 20 + WIDTH * 42,
			22 + WIDTH * 39,
			36 + WIDTH * 11, 42 + WIDTH * 26, 39 + WIDTH * 15,
			43 + WIDTH * 22, 36 + WIDTH * 42, 36 + WIDTH * 39,
			43 + WIDTH * 24, 29 + WIDTH * 35, 8 + WIDTH * 14,
			27 + WIDTH * 17, 33 + WIDTH * 42, 26 + WIDTH * 5,
			20 + WIDTH * 8, 30 + WIDTH * 19, 28 + WIDTH * 8,
			23 + WIDTH * 10, 23 + WIDTH * 11, 39 + WIDTH * 37,
			24 + WIDTH * 6, 40 + WIDTH * 28,
			40 + WIDTH * 26, 7 + WIDTH * 9, 10 + WIDTH * 34
	};

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
		return Assets.Environment.SPS_WATER_SEWERS;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		map = SpringFestivalLayouts.SPRING_FESTIVAL_LAYOUT.clone();
		if (map.length != length()) return false;
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_SPRING, width(), height(), map));
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		map[ENTRANCE] = Terrain.ENTRANCE;
		stockChecked = false;
		return true;
	}

	@Override
	protected void createMobs() {
		for (int i = 0; i < RESTORED_RESIDENTS.length; i++) {
			TownNpc resident = TownNpc.create(RESTORED_RESIDENTS[i]);
			resident.pos = RESTORED_RESIDENT_CELLS[i];
			mobs().add(resident);
		}
		TestMob scarecrow = new TestMob();
		scarecrow.pos = 15 + WIDTH * 3;
		mobs().add(scarecrow);
		YearBeast2 yearBeast = new YearBeast2();
		yearBeast.pos = 6 + WIDTH * 44;
		mobs().add(yearBeast);
	}

	@Override
	protected void createItems() {
		// The source delays both shop rolls until the first occupied cell.
	}

	@Override
	public void occupyCell(Char ch) {
		if (!stockChecked) {
			storeStock();
			stockChecked = true;
		}
		super.occupyCell(ch);
	}

	public void storeStock() {
		for (int cell : supplyStoreSpots) {
			if (heaps.get(cell) == null) {
				drop(festivalSupply(), cell).type = Heap.Type.FOR_SALE;
			}
		}
		for (int cell : jewelryStoreSpots) {
			if (heaps.get(cell) == null) {
				drop(festivalJewelry(), cell).type = Heap.Type.FOR_SALE;
			}
		}
	}

	private Item festivalJewelry() {
		return Generator.random(Random.Int(2) == 0
				? Generator.Category.ARTIFACT
				: Generator.Category.RING);
	}

	private Item festivalSupply() {
		switch (Random.Int(6)) {
			case 0: return Generator.random(Generator.Category.MELEEWEAPON);
			case 1: return Generator.random(Generator.Category.ARMOR);
			case 2: return Generator.random(Generator.Category.POTION);
			case 3: return Generator.random(Generator.Category.WAND);
			case 4: return Generator.random(Generator.Category.SCROLL);
			default: return Generator.random(Generator.Category.HIGHFOOD);
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEGACY_MINE_DEPTH, mineDepth);
		bundle.put(LEGACY_STORE_SPOTS, jewelryStoreSpots);
		bundle.put(LEGACY_SUPPLY_SPOTS, supplyStoreSpots);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		mineDepth = bundle.getInt(LEGACY_MINE_DEPTH);
		jewelryStoreSpots = normalizedSpots(bundle.getIntArray(LEGACY_STORE_SPOTS),
				FESTIVAL_JEWELRY_STORE);
		supplyStoreSpots = normalizedSpots(bundle.getIntArray(LEGACY_SUPPLY_SPOTS),
				FESTIVAL_SUPPLY_STORE);
		stockChecked = false;
	}

	private int[] normalizedSpots(int[] saved, int fallback) {
		if (saved == null || saved.length == 0) return new int[]{fallback};
		for (int cell : saved) {
			if (cell < 0 || cell >= length()) return new int[]{fallback};
		}
		return saved;
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
}
