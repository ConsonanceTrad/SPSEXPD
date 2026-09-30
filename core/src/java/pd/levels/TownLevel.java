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
import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Alter;
import pd.actors.blobs.Blob;
import pd.actors.mobs.AdultDragonViolet;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Piranha;
import pd.actors.mobs.TestMob2;
import pd.actors.mobs.TestMob;
import pd.actors.mobs.npcs.Shopkeeper;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.AdamantArmor;
import pd.items.AdamantRing;
import pd.items.AdamantWand;
import pd.items.AdamantWeapon;
import pd.items.Ankh;
import pd.items.ArmorKit;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.KnowledgeBook;
import pd.items.PocketBall;
import pd.items.bombs.ArcaneBomb;
import pd.items.bombs.Bomb;
import pd.items.bombs.Firebomb;
import pd.items.bombs.FlashBangBomb;
import pd.items.bombs.FrostBomb;
import pd.items.bombs.HolyBomb;
import pd.items.bombs.Noisemaker;
import pd.items.bombs.RegrowthBomb;
import pd.items.bombs.ShrapnelBomb;
import pd.items.bombs.SmokeBomb;
import pd.items.bombs.WoollyBomb;
import pd.items.eggs.Egg;
import pd.items.food.completefood.PetFood;
import pd.items.food.fusion.Nut;
import pd.items.keys.GoldenSkeletonKey;
import pd.items.misc.SkillOfAtk;
import pd.items.misc.SkillOfDef;
import pd.items.misc.SkillOfMig;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.items.quest.DarkGold;
import pd.items.quest.Mushroom;
import pd.items.reward.BoundReward;
import pd.items.scrolls.ScrollOfMagicalInfusion;
import pd.items.scrolls.ScrollOfRegrowth;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.items.summon.ActiveMrDestructo;
import pd.items.summon.FairyCard;
import pd.items.summon.Mobile;
import pd.items.wands.WandOfMagicMissile;
import pd.items.weapon.melee.fusion.Flute;
import pd.items.weapon.melee.fusion.Harp;
import pd.items.weapon.melee.fusion.Triangolo;
import pd.items.weapon.melee.fusion.Trumpet;
import pd.items.weapon.melee.fusion.WarDrum;
import pd.items.weapon.melee.special.FireCracker;
import pd.items.weapon.melee.special.RunicBlade;
import pd.items.weapon.missiles.MoneyPack;
import pd.levels.features.LevelTransition;
import pd.plants.ReNepenth;
import pd.plants.Starflower;
import pd.tiles.custom.SpsLegacyLevelVisual;
import render.utils.math.Random;

import java.util.Calendar;

/** The original 48x48 SPS town hub, journal destination 5. */
public class TownLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 25 + WIDTH * 21;
	public static final int LEGACY_EXIT = 5 + WIDTH * 40;

	public static final int[] BASE_CHESTS = {
			39 + WIDTH * 4, 40 + WIDTH * 4, 41 + WIDTH * 4, 42 + WIDTH * 4,
			7 + WIDTH * 5, 8 + WIDTH * 5, 9 + WIDTH * 5
	};
	public static final int[] TOMB_CHESTS = {
			41 + WIDTH * 18, 42 + WIDTH * 18
	};
	public static final int[] FISH_CHESTS = {
			27 + WIDTH * 11, 27 + WIDTH * 12
	};
	public static final int TOMB_CELL = 41 + WIDTH * 41;
	public static final int ALTAR_CELL = 33 + WIDTH * 32;
	public static final int[] EQUIPMENT_STORE_CELLS = {
			4 + WIDTH * 27, 4 + WIDTH * 26, 4 + WIDTH * 25, 4 + WIDTH * 24,
			4 + WIDTH * 23, 4 + WIDTH * 22, 6 + WIDTH * 25, 7 + WIDTH * 25,
			8 + WIDTH * 25, 9 + WIDTH * 25, 10 + WIDTH * 25
	};
	public static final int[] GENERAL_STORE_CELLS = {
			7 + WIDTH * 10, 7 + WIDTH * 9, 7 + WIDTH * 8, 7 + WIDTH * 7,
			12 + WIDTH * 13, 12 + WIDTH * 12, 12 + WIDTH * 11,
			12 + WIDTH * 10, 12 + WIDTH * 9
	};
	public static final int[] BOMB_STORE_CELLS = {
			45 + WIDTH, 45 + WIDTH * 3, 44 + WIDTH, 44 + WIDTH * 2,
			44 + WIDTH * 3, 46 + WIDTH, 46 + WIDTH * 2, 46 + WIDTH * 3
	};
	public static final int[] FOOD_STORE_CELLS = {
			24 + WIDTH * 6, 25 + WIDTH * 6, 26 + WIDTH * 6, 27 + WIDTH * 6
	};
	public static final int[] SPECIAL_STORE_CELLS = {20 + WIDTH * 6};
	public static final int[] EGG_STORE_CELLS = {41 + WIDTH * 10};
	public static final int[] GNOLL_STORE_CELLS = {21 + WIDTH * 3};
	public static final int[] SKILL_STORE_CELLS = {
			33 + WIDTH * 44, 34 + WIDTH * 44, 35 + WIDTH * 44
	};
	public static final int[] PILL_STORE_CELLS = {43 + WIDTH * 23};
	public static final int[] ARMOR_STORE_CELLS = {43 + WIDTH * 20};
	public static final int[] SHOPKEEPER_CELLS = {
			13 + WIDTH * 10, 8 + WIDTH * 23
	};
	public static final TownNpc.Spec[] RESIDENTS = {
			TownNpc.Spec.UDAWOS, TownNpc.Spec.TYPED_SCROLL, TownNpc.Spec.G2159687,
			TownNpc.Spec.CONSIDERED_HAMSTER, TownNpc.Spec.BILBOLDEV, TownNpc.Spec.XIXI_ZERO,
			TownNpc.Spec.MILLILITRE, TownNpc.Spec.NYRDS, TownNpc.Spec.HBB,
			TownNpc.Spec.SFB, TownNpc.Spec.FLY_LING, TownNpc.Spec.OMICRONRG9,
			TownNpc.Spec.HONEY_POOOOT, TownNpc.Spec.JINKELOID, TownNpc.Spec.ATV9,
			TownNpc.Spec.SP931, TownNpc.Spec.DREAM_PLAYER, TownNpc.Spec.EVAN,
			TownNpc.Spec.ICE13, TownNpc.Spec.HEXA, TownNpc.Spec.COCONUT,
			TownNpc.Spec.LOCASTAN, TownNpc.Spec.GOBLIN_PLAYER, TownNpc.Spec.DACHHACK,
			TownNpc.Spec.OLD_NEW_STWIST, TownNpc.Spec.HATE_SOKOBAN, TownNpc.Spec.LAJI,
			TownNpc.Spec.KOSTIS12345, TownNpc.Spec.APOSTLE,
			TownNpc.Spec.NUT_PAINTER, TownNpc.Spec.JUH9870, TownNpc.Spec.SAD_SALTAN,
			TownNpc.Spec.SHOWER, TownNpc.Spec.RENNPC, TownNpc.Spec.MAYOR, TownNpc.Spec.GEOLOGIST
	};
	public static final int[] RESIDENT_CELLS = {
			33 + WIDTH * 34, 9 + WIDTH * 23, 35 + WIDTH * 3,
			26 + WIDTH * 10, 24 + WIDTH * 10, 25 + WIDTH * 11,
			45 + WIDTH * 42, 13 + WIDTH * 11, 43 + WIDTH * 15,
			33 + WIDTH * 11, 37 + WIDTH * 12, 36 + WIDTH * 11,
			40 + WIDTH * 15, 43 + WIDTH * 14, 43 + WIDTH * 17,
			40 + WIDTH * 16, 43 + WIDTH * 24, 27 + WIDTH * 31,
			29 + WIDTH * 32, 33 + WIDTH * 30, 45 + WIDTH * 2,
			5 + WIDTH * 5, 2 + WIDTH * 5, 37 + WIDTH * 3,
			20 + WIDTH * 3, 23 + WIDTH * 10, 23 + WIDTH * 12,
			20 + WIDTH * 25, 20 + WIDTH * 21,
			16 + WIDTH * 21, 35 + WIDTH * 6, 42 + WIDTH * 38,
			30 + WIDTH * 19, 28 + WIDTH * 27, 31 + WIDTH * 20, 14 + WIDTH * 33
	};
	public static final TownNpc.Spec[] TOMB_RESIDENTS = {
			TownNpc.Spec.WATABOU, TownNpc.Spec.WHITE_GHOST
	};
	public static final int[] TOMB_RESIDENT_CELLS = {
			42 + WIDTH * 42, 45 + WIDTH * 44
	};
	public static final TownNpc.Spec[] UNCLE_RESIDENTS = {
			TownNpc.Spec.UNCLE_S, TownNpc.Spec.A_REAL_MAN,
			TownNpc.Spec.LYN, TownNpc.Spec.SAID_BY_SUN
	};
	public static final int[] UNCLE_RESIDENT_CELLS = {
			43 + WIDTH * 22, 44 + WIDTH * 45, 40 + WIDTH * 22, 42 + WIDTH * 45
	};
	public static final TownNpc.Spec[] EGG_RESIDENTS = {
			TownNpc.Spec.LERY, TownNpc.Spec.BLACK_MEOW, TownNpc.Spec.CAT_SHEEP
	};
	public static final int[] EGG_RESIDENT_CELLS = {
			41 + WIDTH * 9, 38 + WIDTH * 38, 37 + WIDTH * 37
	};
	public static final TownNpc.Spec[] MOS_RESIDENTS = {
			TownNpc.Spec.MEMORY_OF_SAND, TownNpc.Spec.A_FLY, TownNpc.Spec.BONE_STAR
	};
	public static final int[] MOS_RESIDENT_CELLS = {
			26 + WIDTH * 4, 27 + WIDTH * 17, 21 + WIDTH * 9
	};
	public static final TownNpc.Spec[] SAR_RESIDENTS = {
			TownNpc.Spec.STORM_AND_RAIN, TownNpc.Spec.RAVENWOLF
	};
	public static final int[] SAR_RESIDENT_CELLS = {
			21 + WIDTH * 6, 23 + WIDTH * 6
	};
	public static final TownNpc.Spec[] RAIN_RESIDENTS = {
			TownNpc.Spec.RAIN_TRAINER, TownNpc.Spec.RUSTYBLADE, TownNpc.Spec.TEMPEST102
	};
	public static final int[] RAIN_RESIDENT_CELLS = {
			17 + WIDTH * 42, 22 + WIDTH * 42, 33 + WIDTH * 42
	};
	public static final TownNpc.Spec[] FISH_RESIDENTS = {
			TownNpc.Spec.ALIVE_FISH, TownNpc.Spec.ASH_WOLF
	};
	public static final int[] FISH_RESIDENT_CELLS = {
			42 + WIDTH * 9, 40 + WIDTH * 36
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
		return isWarmSeason() ? Assets.Environment.SPS_WATER_PRISON
				: Assets.Environment.SPS_WATER_SNOW;
	}

	private static boolean isWarmSeason() {
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		return month > 2 && month < 11;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		map = TownLayouts.TOWN_LAYOUT.clone();
		String texture = isWarmSeason() ? Assets.Environment.SPS_TILES_TOWN
				: Assets.Environment.SPS_TILES_SNOW_TOWN;
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(texture, width(), height(), map));
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.BRANCH_ENTRANCE,
				Dungeon.depth, 0, LevelTransition.Type.REGULAR_ENTRANCE));
		map[ENTRANCE] = Terrain.ENTRANCE;
		return true;
	}

	@Override
	protected void createMobs() {
		for (int cell : SHOPKEEPER_CELLS) {
			Shopkeeper shopkeeper = new Shopkeeper();
			shopkeeper.pos = cell;
			mobs().add(shopkeeper);
		}
		for (int i = 0; i < RESIDENTS.length; i++) {
			TownNpc.Spec spec = RESIDENTS[i];
			if (spec == TownNpc.Spec.COCONUT && Badges.checkCoconutRescued()) {
				spec = TownNpc.Spec.FRUIT_CAT;
			}
			TownNpc resident = TownNpc.create(spec);
			resident.pos = RESIDENT_CELLS[i];
			mobs().add(resident);
		}
		if (Badges.checkTombRescued()) addResidents(TOMB_RESIDENTS, TOMB_RESIDENT_CELLS);
		if (Badges.checkUncleRescued()) addResidents(UNCLE_RESIDENTS, UNCLE_RESIDENT_CELLS);
		if (Badges.checkEggRescued()) addResidents(EGG_RESIDENTS, EGG_RESIDENT_CELLS);
		if (Badges.checkMOSRescued()) addResidents(MOS_RESIDENTS, MOS_RESIDENT_CELLS);
		if (Badges.checkMOSRescued()) {
			Piranha piranha = new Piranha();
			piranha.pos = 19 + WIDTH * 9;
			mobs().add(piranha);
		}
		if (Badges.checkSARRescued()) addResidents(SAR_RESIDENTS, SAR_RESIDENT_CELLS);
		if (Dungeon.dewNorn) {
			addResidents(new TownNpc.Spec[]{TownNpc.Spec.LYNN},
					new int[]{31 + WIDTH * 32});
		}
		if (Badges.checkRainRescued()) {
			addResidents(RAIN_RESIDENTS, RAIN_RESIDENT_CELLS);
			TestMob testMob = new TestMob();
			testMob.pos = 18 + WIDTH * 44;
			mobs().add(testMob);
		}
		if (Badges.checkFishRescued()) {
			addResidents(FISH_RESIDENTS, FISH_RESIDENT_CELLS);
			for (int cell : new int[]{42 + WIDTH * 37, 42 + WIDTH * 36}) {
				Piranha piranha = new Piranha();
				piranha.pos = cell;
				mobs().add(piranha);
			}
		}
		AdultDragonViolet dragon = new AdultDragonViolet();
		dragon.pos = 5 + WIDTH * 43;
		mobs().add(dragon);
		TestMob2 clockwork = new TestMob2();
		clockwork.pos = 21 + WIDTH * 44;
		mobs().add(clockwork);
		if (Badges.checkOtilukeRescued() && Actor.findChar(32 + WIDTH * 15) == null) {
			TownNpc otiluke = TownNpc.create(TownNpc.Spec.OTILUKE_NPC);
			otiluke.pos = 32 + WIDTH * 15;
			mobs().add(otiluke);
		}
	}

	private void addResidents(TownNpc.Spec[] specs, int[] cells) {
		for (int i = 0; i < specs.length; i++) {
			TownNpc resident = TownNpc.create(specs[i]);
			resident.pos = cells[i];
			mobs().add(resident);
		}
	}

	@Override
	protected void createItems() {
		if (Dungeon.dewNorn) Blob.seed(ALTAR_CELL, 1, Alter.class, this);
		for (int cell : BASE_CHESTS) addChest(cell);
		if (Badges.checkTombRescued()) {
			for (int cell : TOMB_CHESTS) addChest(cell);
			addTomb(TOMB_CELL);
		}
		if (Badges.checkFishRescued()) {
			for (int cell : FISH_CHESTS) addChest(cell);
		}
		if (Badges.checkItemRescued()) {
			addShopStock(GENERAL_STORE_CELLS, 0);
			addShopStock(EQUIPMENT_STORE_CELLS, 1);
		}
		if (Badges.checkCoconutRescued()) addShopStock(BOMB_STORE_CELLS, 2);
		if (Badges.checkMOSRescued()) addShopStock(FOOD_STORE_CELLS, 3);
		if (Badges.checkSARRescued()) addShopStock(SPECIAL_STORE_CELLS, 4);
		if (Badges.checkEggRescued()) addShopStock(EGG_STORE_CELLS, 5);
		if (Dungeon.gnollMission) addShopStock(GNOLL_STORE_CELLS, 6);
		if (Badges.checkRainRescued()) addShopStock(SKILL_STORE_CELLS, 7);
		if (Badges.checkUncleRescued()) addShopStock(PILL_STORE_CELLS, 8);
		if (Badges.checkFishRescued()) addShopStock(ARMOR_STORE_CELLS, 9);
	}

	@Override
	public void pressCell(int cell) {
		super.pressCell(cell);
		if (Dungeon.dewNorn && cell == ALTAR_CELL) Alter.transmute(cell);
	}

	private void addShopStock(int[] cells, int pool) {
		for (int cell : cells) {
			if (heaps.get(cell) == null) drop(shopItem(pool), cell).type = Heap.Type.FOR_SALE;
		}
	}

	public Item shopItem(int pool) {
		switch (pool) {
			case 0: return generalStoreItem();
			case 1: return equipmentStoreItem();
			case 2: return bombStoreItem();
			case 3: return Random.Int(3) == 1 ? Generator.random(Generator.Category.FOOD) : Generator.random(Generator.Category.HIGHFOOD);
			case 4:
				switch (Random.Int(6)) {
					case 0: return new AdamantArmor();
					case 1: return new AdamantRing();
					case 2: return new AdamantWand();
					case 3: return new AdamantWeapon();
					default: return new DarkGold().quantity(10);
				}
			case 5: return new Egg();
			case 6:
				switch (Random.Int(10)) {
					case 0: return new MoneyPack(3);
					case 1: return new WandOfMagicMissile();
					default: return Generator.random(Generator.Category.EASTERWEAPON);
				}
			case 7:
				switch (Random.Int(6)) {
					case 0: return new SkillOfAtk();
					case 1: return new SkillOfDef();
					case 2: return new SkillOfMig();
					case 3: return new KnowledgeBook();
					default: return musicWeapon();
				}
			case 8: return Generator.random(Generator.Category.MEDICINE);
			default: return new ArmorKit();
		}
	}

	private Item generalStoreItem() {
		switch (Random.Int(10)) {
			case 0: return new Mushroom();
			case 1: return Generator.random(Generator.Category.POTION);
			case 2: return new PocketBall();
			case 3: return Generator.random(Generator.Category.SCROLL);
			case 4: return Generator.random(Generator.Category.SEED);
			case 5: return Generator.random(Generator.Category.BERRY);
			case 6: return summonItem();
			case 7: return new PetFood();
			case 8: return new BoundReward();
			default: return new Nut();
		}
	}

	private Item equipmentStoreItem() {
		switch (Random.Int(12)) {
			case 0: return new ScrollOfUpgrade();
			case 1: return new ScrollOfMagicalInfusion();
			case 2:
			case 9: return Generator.random(Generator.Category.MISSILE);
			case 3: return Generator.random(Generator.Category.WAND);
			case 4: return Generator.random(Generator.Category.RING);
			case 5: return Generator.random(Generator.Category.MELEEWEAPON);
			case 6:
				switch (Random.Int(5)) {
					case 0: return new BlueNornStone();
					case 1: return new GreenNornStone();
					case 2: return new OrangeNornStone();
					case 3: return new PurpleNornStone();
					default: return new YellowNornStone();
				}
			case 7: return Generator.random(Generator.Category.ARMOR);
			case 8: return new RunicBlade();
			case 10: return new GoldenSkeletonKey();
			default: return new ScrollOfUpgrade();
		}
	}

	private Item bombStoreItem() {
		int roll = Random.Int(12);
		if (roll == 0) return new Ankh();
		if (roll == 11) return new ScrollOfRegrowth();
		switch (Random.Int(11)) {
			case 0: return new Bomb();
			case 1: return new ArcaneBomb();
			case 2: return new Firebomb();
			case 3: return new FlashBangBomb();
			case 4: return new FrostBomb();
			case 5: return new HolyBomb();
			case 6: return new Noisemaker();
			case 7: return new RegrowthBomb();
			case 8: return new ShrapnelBomb();
			case 9: return new SmokeBomb();
			default: return new WoollyBomb();
		}
	}

	private Item summonItem() {
		switch (Random.Int(3)) {
			case 0: return new FairyCard();
			case 1: return new Mobile();
			default: return new ActiveMrDestructo();
		}
	}

	private Item musicWeapon() {
		switch (Random.Int(5)) {
			case 0: return new Triangolo();
			case 1: return new Flute();
			case 2: return new WarDrum();
			case 3: return new Trumpet();
			default: return new Harp();
		}
	}

	private void addTomb(int cell) {
		Item prize;
		switch (Random.Int(10)) {
			case 0:
				prize = new ReNepenth.Seed();
				break;
			case 1:
				prize = new Mushroom();
				break;
			default:
				prize = new DarkGold();
				break;
		}
		drop(prize, cell).type = Heap.Type.TOMB;
	}

	private void addChest(int cell) {
		Item prize;
		switch (Random.Int(10)) {
			case 0:
				prize = Generator.random(Generator.Category.BERRY);
				break;
			case 1:
				prize = new Starflower.Seed();
				break;
			default:
				prize = new DarkGold();
				break;
		}
		drop(prize, cell).type = Heap.Type.CHEST;
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
