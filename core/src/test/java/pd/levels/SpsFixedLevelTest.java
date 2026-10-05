package pd.levels;

import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroAction;
import pd.actors.hero.HeroClass;
import pd.actors.mobs.AdultDragonViolet;
import pd.actors.mobs.BanditKing;
import pd.actors.mobs.BlueWraith;
import pd.actors.mobs.CrabKing;
import pd.actors.mobs.Dragonking;
import pd.actors.mobs.ElderAvatar;
import pd.actors.mobs.Fiend;
import pd.actors.mobs.FlyingProtector;
import pd.actors.mobs.GnollArcher;
import pd.actors.mobs.GnollKing;
import pd.actors.mobs.GoldOrc;
import pd.actors.mobs.Hybrid;
import pd.actors.mobs.King;
import pd.actors.mobs.LichDancer;
import pd.actors.mobs.LitTower;
import pd.actors.mobs.MagicEye;
import pd.actors.mobs.MineSentinel;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Orc;
import pd.actors.mobs.Otiluke;
import pd.actors.mobs.PlagueDoctor;
import pd.actors.mobs.PrisonWander;
import pd.actors.mobs.SewerHeart;
import pd.actors.mobs.ShadowYog;
import pd.actors.mobs.Shell;
import pd.actors.mobs.SkeletonHand1;
import pd.actors.mobs.SkeletonHand2;
import pd.actors.mobs.SkeletonKing;
import pd.actors.mobs.SpiderQueen;
import pd.actors.mobs.SpsDM300;
import pd.actors.mobs.SpsGoo;
import pd.actors.mobs.SpsHermitCrab;
import pd.actors.mobs.SpsPrisonMobs;
import pd.actors.mobs.SpsTengu;
import pd.actors.mobs.Tank;
import pd.actors.mobs.TenguDen;
import pd.actors.mobs.TestMob2;
import pd.actors.mobs.TestMob;
import pd.actors.mobs.ThiefKing;
import pd.actors.mobs.UAmulet;
import pd.actors.mobs.UDM300;
import pd.actors.mobs.UGoo;
import pd.actors.mobs.UIcecorps2;
import pd.actors.mobs.UIcecorps;
import pd.actors.mobs.UKing;
import pd.actors.mobs.UTengu;
import pd.actors.mobs.UYog;
import pd.actors.mobs.YearBeast2;
import pd.actors.mobs.Yog;
import pd.actors.mobs.Zot;
import pd.actors.mobs.ZotPhase;
import pd.actors.mobs.npcs.SheepSokoban;
import pd.actors.mobs.npcs.SheepSokobanBlack;
import pd.actors.mobs.npcs.SheepSokobanCorner;
import pd.actors.mobs.npcs.SheepSokobanSwitch;
import pd.actors.mobs.npcs.SpsSokobanSheep;
import pd.actors.mobs.npcs.TownNpc;
import pd.actors.mobs.pets.YearPet;
import pd.items.AdamantRing;
import pd.items.Ankh;
import pd.items.BossRush;
import pd.items.Garbage;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.Palantir;
import pd.items.PowerHand;
import pd.items.SoulCollect;
import pd.items.StoneOre;
import pd.items.TreasureMap;
import pd.items.YellowDewdrop;
import pd.items.equipment.armor.ClothArmor;
import pd.items.equipment.bags.HeartOfScarecrow;
import pd.items.consum.eggs.AflyEgg;
import pd.items.consum.eggs.ShadowDragonEgg;
import pd.items.consum.eggs.YearPetEgg;
import pd.items.consum.food.AflyFood;
import pd.items.consum.food.ChargrilledMeat;
import pd.items.consum.food.SmallMeat;
import pd.items.consum.food.SmallRation;
import pd.items.consum.food.fruit.Strawberry;
import pd.items.consum.food.vegetable.HealGrass;
import pd.items.specific.keys.CrystalKey;
import pd.items.specific.keys.GoldenKey;
import pd.items.specific.keys.GoldenSkeletonKey;
import pd.items.specific.keys.IronKey;
import pd.items.misc.AutoPotion;
import pd.items.misc.GnollMark;
import pd.items.misc.LuckyBadge;
import pd.items.misc.MissileShield;
import pd.items.misc.PotionOfMage;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.NornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.items.quest.AdventureJournal;
import pd.items.quest.GnollClothes;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.skills.ClassSkill;
import pd.items.skills.RogueSkill;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.Dagger;
import pd.items.equipment.weapon.melee.relic.AresSword;
import pd.items.equipment.weapon.melee.relic.CromCruachAxe;
import pd.items.equipment.weapon.melee.relic.JupitersWraith;
import pd.items.equipment.weapon.melee.relic.LokisFlail;
import pd.items.equipment.weapon.melee.relic.NeptunusTrident;
import pd.items.equipment.weapon.rockcode.Dpotion;
import pd.items.equipment.weapon.rockcode.Gleaf;
import pd.journal.Notes;
import pd.levels.Transitions;
import pd.sprites.CharSprite;
import pd.tiles.custom.SpsLegacyLevelVisual;
import pd.windows.WndAflyInfo;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/** Geometry checks for SPS fixed levels which do not require a rendering context. */
public final class SpsFixedLevelTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		render.utils.serialize.FileUtils.setDefaultFileProperties(com.badlogic.gdx.Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-fixed-levels" + java.io.File.separator);
		pd.Badges.loadGlobal();
		Game.version = "test";
		Game.versionCode = pd.ShatteredPixelDungeon.v4_0_0;
		pd.items.consum.scrolls.Scroll.initLabels();
		pd.items.consum.potions.Potion.initColors();
		pd.items.equipment.rings.Ring.initGems();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Random.pushGenerator(0x535053L);
		try {
			DeadEndLevel level = new DeadEndLevel();
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			check(level.build(), "DeadEndLevel构建失败");
			check(level.width() == 48 && level.height() == 48, "DeadEndLevel必须为48x48");
			int entrance = 5 * 48 + 3;
			check(level.map[entrance] == Terrain.ENTRANCE, "DeadEndLevel入口坐标错误");
			check(level.map[3 * 49] == Terrain.SIGN, "DeadEndLevel告示牌坐标错误");
			check(level.customTiles.size() == 1, "DeadEndLevel告示牌图块缺失");
			check(level.randomRespawnCell(null) == entrance - 48, "DeadEndLevel重生点错误");
			for (int i = 1; i <= 5; i++) {
				check(level.map[48 + i] == Terrain.WATER, "DeadEndLevel上侧水环错误");
				check(level.map[48 * 5 + i] == (i == 3 ? Terrain.ENTRANCE : Terrain.WATER),
						"DeadEndLevel下侧水环错误");
				check(level.map[48 * i + 1] == Terrain.WATER, "DeadEndLevel左侧水环错误");
				check(level.map[48 * i + 5] == Terrain.WATER, "DeadEndLevel右侧水环错误");
			}
		} finally {
			Random.popGenerator();
		}
		testSafeLevels();
		testBetweenLevelTextures();
		testFleecingTrap();
		testSokobanIntroLevel();
		testSokobanCastle();
		testSokobanTeleportLevel();
		testSokobanPuzzlesLevel();
		testSokobanRuntimeAndPersistence();
		testTownLevel();
		testNornAltar();
		testSpringFestivalLevel();
		testMinesBossLevel();
		testNewRoomLevel();
		testInfestBossLevel();
		testTenguDenLevel();
		testSkeletonAndCrabBossLevels();
		testThiefBossLevel();
		testPotLevel();
		testShadowEaterLevel();
		testDragonCaveLevel();
		testThiefCatchLevel();
		testRoomOfZotLevel();
		testChaosLevel();
		testBossRushLevel();
		testGnollKingQuest();
		testZotPrison();
		testMainBossLayouts();
		testLegacyBossProperties();
		testCavesBossLevel();
		testCityBossGeometry();
		testHallsBossLevel();
		testMissingMainBossRecovery();
		testOrphanSewerMinionRecovery();
		testFestivalDrops();
		System.out.println("SPS固定地图测试通过：DeadEndLevel、SafeLevel三套布局、四张推箱地图、城镇、春节镇、主线五张首领图、能源核心、新居、寄生虫巢、天狗隐匿处、骷髅王陵、巨蟹王巢、盗贼王据点、原野竞技场、蜂蜜陶罐房、暗噬避难所、龙穴、盗贼追捕关、BossRush、豺狼王竞技场及Zot监牢均符合旧版结构。");
	}

	private static void testBetweenLevelTextures() {
		int[] depths = {0, 6, 11, 16, 21};
		String[] tiles = {Assets.Environment.TILES_SEWERS, Assets.Environment.TILES_PRISON,
				Assets.Environment.TILES_CAVES, Assets.Environment.TILES_CITY,
				Assets.Environment.TILES_HALLS};
		String[] legacyTiles = {Assets.Environment.SPS_TILES_SEWERS_LEGACY,
				Assets.Environment.SPS_TILES_PRISON_LEGACY, Assets.Environment.SPS_TILES_BEACH,
				Assets.Environment.SPS_TILES_CITY_LEGACY, Assets.Environment.SPS_TILES_HALLS_LEGACY};
		String[] waters = {Assets.Environment.SPS_WATER_SEWERS, Assets.Environment.SPS_WATER_PRISON,
				Assets.Environment.SPS_WATER_CAVES, Assets.Environment.SPS_WATER_CITY,
				Assets.Environment.SPS_WATER_HALLS};
		for (int i = 0; i < depths.length; i++) {
			Dungeon.depth = depths[i];
			BetweenLevel level = new BetweenLevel();
			check(tiles[i].equals(level.tilesTex()), "过渡层" + depths[i] + "现代底图资源错误");
			check(java.util.Objects.equals(legacyTiles[i], level.legacyTilesTex()),
					"过渡层" + depths[i] + "旧版覆盖图资源错误");
			check(waters[i].equals(level.waterTex()), "过渡层" + depths[i] + "水面资源错误");
		}
		int[] betweenTerrain = {Terrain.CHASM, Terrain.EMPTY, Terrain.GRASS, Terrain.EMPTY_WELL,
				Terrain.WALL, Terrain.DOOR, Terrain.OPEN_DOOR, Terrain.ENTRANCE, Terrain.EXIT,
				Terrain.EMBERS, Terrain.LOCKED_DOOR, Terrain.PEDESTAL, Terrain.WALL_DECO,
				Terrain.BARRICADE, Terrain.EMPTY_SP, Terrain.HIGH_GRASS, Terrain.SECRET_DOOR,
				Terrain.SECRET_TRAP, Terrain.TRAP, Terrain.INACTIVE_TRAP, Terrain.EMPTY_DECO,
				Terrain.LOCKED_EXIT, Terrain.UNLOCKED_EXIT, Terrain.WELL, Terrain.STATUE,
				Terrain.STATUE_SP, Terrain.WATER, Terrain.ALCHEMY, Terrain.BOOKSHELF,
				Terrain.DEW_BLESS, Terrain.TENT, Terrain.IRON_MAKER, Terrain.SIGN,
				Terrain.GROUND_A, Terrain.FLOWER_POT, Terrain.WALL_GROUND, Terrain.WALL_LIVER,
				Terrain.BUY_WALL, Terrain.WALL_SP, Terrain.OLD_HIGH_GRASS, Terrain.BROKEN_DOOR,
				Terrain.BED, Terrain.SHRUB, Terrain.GLASS_WALL};
		for (int terrain : betweenTerrain) {
			int visual = SpsLegacyLevelVisual.terrainVisual(terrain);
			check(visual >= 0 && visual < 64,
					"旧版四行地砖索引越界：terrain=" + terrain + ", visual=" + visual);
		}
	}

	private static void testChaosLevel() {
		Dungeon.depth = 25;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(22);
		pd.actors.hero.Hero previousHero = Dungeon.hero;
		Dungeon.hero = new pd.actors.hero.Hero();
		for (int seed = 0; seed < 250; seed++) {
			Random.pushGenerator(0x4348414F534C564CL + seed);
			try {
				Dungeon.level = null;
				ChaosLevel level = new ChaosLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.mobs().clear();
				level.heaps = new SparseArray<>();
				level.blobs = new java.util.HashMap<>();
				level.plants = new SparseArray<>();
				level.traps = new SparseArray<>();
				level.customTerrain = new ArrayList<>();
				level.customWalls = new ArrayList<>();
				check(level.build(), "Chaos层构建失败，种子=" + seed);
				level.buildFlagMaps();
				check(level.width() == 48 && level.height() == 48, "Chaos层必须为48x48");
				check(level.legacyRoomCount() >= 20, "Chaos层BSP房间少于20个");
				check(level.legacySpecialRoomCount() == 0,
						"Chaos层混入了旧版深度51至99禁用的普通特殊房");
				check(level.legacyGenerationAttempts() <= 64, "Chaos层生成超过重试上限");
				check(level.map[level.entrance()] == Terrain.PEDESTAL, "Chaos层入口必须为旧版基座");
				check(level.map[level.exit()] == Terrain.PEDESTAL, "Chaos层出口必须为旧版基座");
				check(level.transitions.size() == 2, "Chaos层必须同时保留入口与出口过渡");
				boolean hasLegacyVisual = false;
				for (pd.tiles.CustomTilemap tile : level.customTiles) {
					if (tile instanceof SpsLegacyLevelVisual) hasLegacyVisual = true;
				}
				check(hasLegacyVisual, "Chaos层原始魔法洞窟图层缺失");
				for (int terrain : level.map) check(terrain != Terrain.CHASM, "Chaos层必须清除深渊地形");
				check(reachable(level, level.entrance(), true)[level.exit()],
						"Chaos层入口无法到达出口，种子=" + seed);
			} finally {
				Random.popGenerator();
			}
		}
		Class<? extends Mob>[] pool = ChaosLevel.legacyMobPool();
		HashSet<Class<? extends Mob>> poolTypes = new HashSet<>(Arrays.asList(pool));
		check(pool.length == 43 && poolTypes.size() == 43, "Chaos层必须使用旧版43种等权怪物");
		check(poolTypes.contains(BlueWraith.class) && poolTypes.contains(Orc.class)
				&& poolTypes.contains(FlyingProtector.class), "Chaos层缺少试炼区域敌人");
		BlueWraith blueWraith = new BlueWraith();
		Orc orc = new Orc();
		FlyingProtector protector = new FlyingProtector();
		check(blueWraith.HT == 250 && blueWraith.defenseSkill == 24, "怨灵战士旧版数值错误");
		check(orc.HT == 400 && orc.defenseSkill == 30, "猪人旧版数值错误");
		check(protector.HT == 390 && protector.defenseSkill == 89, "Chaos智慧守卫旧版深度缩放错误");

		Random.pushGenerator(0x4348414F534D4F42L);
		try {
			ChaosLevel level = new ChaosLevel();
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			level.customTerrain = new ArrayList<>();
			level.customWalls = new ArrayList<>();
			level.mobs().clear();
			level.heaps = new SparseArray<>();
			level.blobs = new java.util.HashMap<>();
			level.plants = new SparseArray<>();
			level.traps = new SparseArray<>();
			level.plants = new SparseArray<>();
			level.traps = new SparseArray<>();
			level.customTerrain = new ArrayList<>();
			level.customWalls = new ArrayList<>();
			check(level.build(), "Chaos怪物生成测试地图构建失败");
			level.buildFlagMaps();
			Dungeon.level = level;
			HashSet<Mob> existingMobs = new HashSet<>(level.mobs().all());
			int existing = existingMobs.size();
			level.createMobs();
			int generated = level.mobs().size() - existing;
			check(generated >= 16 && generated <= 18, "Chaos层旧版怪物数量错误：" + generated);
			for (Mob mob : level.mobs()) {
				if (!existingMobs.contains(mob)) {
					check(poolTypes.contains(mob.getClass()), "Chaos层生成了旧版池外怪物：" + mob.getClass());
				}
			}
		} finally {
			Random.popGenerator();
		}
		Dungeon.level = null;
		Dungeon.hero = previousHero;
	}

	private static void testRoomOfZotLevel() {
		Hero previousHero = Dungeon.hero;
		Dungeon.depth = 24;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(20);
		Dungeon.level = null;
		Dungeon.hero = null;
		Random.pushGenerator(0x524F4F4D5A4F5400L);
		try {
			RoomOfZotLevel level = new RoomOfZotLevel();
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			level.mobs().clear();
			level.heaps = new SparseArray<>();
			check(level.build(), "Zot藏宝屋构建失败");
			check(level.width() == 48 && level.height() == 48, "Zot藏宝屋必须为48x48");
			check(level.entrance() == RoomOfZotLevel.ENTRANCE, "Zot藏宝屋入口坐标错误");
			check(level.firstVisitForTesting(), "无日志角色进入Zot藏宝屋应按首次进入处理");
			check(level.customTiles.size() == 1
					&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
					"Zot藏宝屋原始整图图层缺失");
			for (int cell = 0; cell < level.length(); cell++) {
				if (cell != RoomOfZotLevel.ENTRANCE) {
					check(level.map[cell] == SaveRoomLayouts.ROOM_OF_GRASS[cell],
							"Zot藏宝屋固定地形不一致，格=" + cell);
				}
			}
			level.createMobs();
			check(level.mobs().isEmpty(), "Zot藏宝屋旧版空配置不应生成羊或敌人");
			level.createItems();
			int badges = 0;
			int chests = 0;
			for (Heap heap : level.heaps.valueList()) {
				chests++;
				check(heap.type == Heap.Type.CHEST, "Zot藏宝屋掉落必须装在宝箱中");
				pd.items.Item item = heap.peek();
				check(item instanceof Gold || item instanceof LuckyBadge
						|| item instanceof pd.items.consum.scrolls.ScrollOfUpgrade,
						"Zot藏宝屋生成了旧版列表之外的物品");
				if (item instanceof LuckyBadge) badges++;
			}
			check(chests > 0, "Zot藏宝屋固定种子未生成任何宝箱");
			check(badges == 1, "Zot藏宝屋首次进入必须恰好生成一枚幸运胸章");
		} finally {
			Random.popGenerator();
			Dungeon.level = null;
		}

		Hero completedHero = new Hero();
		AdventureJournal completedJournal = new AdventureJournal();
		Bundle completedState = new Bundle();
		completedState.put("completed", 1 << 20);
		completedJournal.restoreFromBundle(completedState);
		completedHero.belongings.backpack.items.add(completedJournal);
		Dungeon.hero = completedHero;
		int revisitGold = 0;
		for (int seed = 0; seed < 8; seed++) {
			Random.pushGenerator(0x524F4F4D52455600L + seed);
			try {
				RoomOfZotLevel revisit = new RoomOfZotLevel();
				revisit.transitions = new ArrayList<>();
				revisit.customTiles = new ArrayList<>();
				revisit.mobs().clear();
				revisit.heaps = new SparseArray<>();
				check(revisit.build(), "Zot藏宝屋重访构建失败，种子=" + seed);
				check(!revisit.firstVisitForTesting(), "Zot藏宝屋完成后仍按首次进入处理");
				revisit.createItems();
				for (Heap heap : revisit.heaps.valueList()) {
					check(heap.type == Heap.Type.CHEST, "Zot藏宝屋重访掉落必须装在宝箱中");
					check(heap.peek() instanceof Gold, "Zot藏宝屋重访生成了首次限定奖励");
					check(heap.peek().quantity() >= 1 && heap.peek().quantity() < 10,
							"Zot藏宝屋重访金币未恢复为1至9");
					revisitGold++;
				}
			} finally {
				Random.popGenerator();
			}
		}
		check(revisitGold > 0, "Zot藏宝屋重访固定种子未覆盖金币奖励");
		Dungeon.hero = previousHero;
	}

	private static void testThiefCatchLevel() {
		Dungeon.depth = 20;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(18);
		for (int seed = 0; seed < 250; seed++) {
			Random.pushGenerator(0x54484945464C564CL + seed);
			try {
				Dungeon.level = null;
				ThiefCatchLevel level = new ThiefCatchLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.mobs().clear();
				level.heaps = new SparseArray<>();
				level.blobs = new java.util.HashMap<>();
				level.traps = new SparseArray<>();
				check(level.build(), "盗贼追捕关构建失败，种子=" + seed);
				level.buildFlagMaps();
				Dungeon.level = level;
				check(level.width() == 48 && level.height() == 48, "盗贼追捕关必须为48x48");
				check(level.standardRoomCountForTesting() == 5, "盗贼追捕关标准房数量错误");
				check(!level.entranceHasConnectedRoomAboveForTesting(), "盗贼追捕关入口顶部错误连通");
				check(level.map[level.returnCellForTesting()] == Terrain.EMPTY_SP,
						"盗贼追捕关旧版返回格地形错误");
				check(Transitions.get( level, null).type
						== pd.levels.features.LevelTransition.Type.BRANCH_ENTRANCE,
						"盗贼追捕关缺少支线返回入口");
				level.createMobs();
				check(countMobs(level, BanditKing.class) == 1,
						"盗贼追捕关必须且只能生成一名蓝衣神偷");
				Mob king = null;
				for (Mob mob : level.mobs()) if (mob instanceof BanditKing) king = mob;
				check(king != null && reachable(level, level.entrance(), true)[king.pos],
						"盗贼追捕关目标无法从入口抵达");
				int entrance = level.entrance();
				level.seal();
				check(level.locked && level.map[entrance] == Terrain.WATER, "盗贼追捕关封锁失败");
				level.unseal();
				check(!level.locked && level.map[entrance] == Terrain.ENTRANCE, "盗贼追捕关解锁失败");
			} finally {
				Random.popGenerator();
			}
		}
		Dungeon.level = null;
	}

	private static void testDragonCaveLevel() {
		Hero previousHero = Dungeon.hero;
		Dungeon.depth = 20;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(17);
		Random.pushGenerator(0x445241474F4E0000L);
		try {
			DragonCaveLevel level = new DragonCaveLevel();
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			level.mobs().clear();
			level.heaps = new SparseArray<>();
			level.blobs = new java.util.HashMap<>();
			level.traps = new SparseArray<>();
			check(level.build(), "龙穴构建失败");
			level.buildFlagMaps();
			Dungeon.level = level;
			check(level.width() == 48 && level.height() == 48, "龙穴必须为48x48");
			check(level.entrance() == DragonCaveLevel.ENTRANCE, "龙穴出生位置错误");
			int prizeCells = 0;
			for (int cell = 0; cell < level.length(); cell++) {
				check(level.map[cell] == SpringFestivalLayouts.DRAGON_CAVE[cell],
						"龙穴固定地形不一致，格=" + cell);
				if (level.map[cell] == Terrain.SOKOBAN_HEAP) prizeCells++;
			}
			check(level.customTiles.size() == 1
					&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
					"龙穴原始谜题图层缺失");
			check(prizeCells > 0, "龙穴必须包含旧版骸骨财宝点");
			check(level.passable[DragonCaveLevel.EGG_POS], "龙穴暗之龙魂坐标不可到达");
			check(new ShadowDragonEgg().lights == 20, "暗之龙魂必须预置20点光能");

			Dungeon.hero = null;
			Dungeon.level = null;
			level.createItems();
			check(level.heaps.get(DragonCaveLevel.EGG_POS) != null
					&& level.heaps.get(DragonCaveLevel.EGG_POS).peek() instanceof ShadowDragonEgg,
					"龙穴首次进入未生成暗之龙魂");
			checkDragonCaveTreasure(level, true);

			DragonCaveLevel revisit = new DragonCaveLevel();
			revisit.transitions = new ArrayList<>();
			revisit.customTiles = new ArrayList<>();
			revisit.mobs().clear();
			revisit.heaps = new SparseArray<>();
			revisit.blobs = new java.util.HashMap<>();
			revisit.traps = new SparseArray<>();
			check(revisit.build(), "龙穴重访地图构建失败");
			revisit.buildFlagMaps();
			Hero completedHero = new Hero();
			AdventureJournal completedJournal = new AdventureJournal();
			Bundle journalState = new Bundle();
			journalState.put("completed", 1 << 17);
			completedJournal.restoreFromBundle(journalState);
			completedHero.belongings.backpack.items.add(completedJournal);
			Dungeon.hero = completedHero;
			Dungeon.level = null;
			revisit.createItems();
			check(revisit.heaps.get(DragonCaveLevel.EGG_POS) == null,
					"龙穴重访错误重复生成暗之龙魂");
			checkDragonCaveTreasure(revisit, false);
		} finally {
			Random.popGenerator();
			Dungeon.hero = previousHero;
			Dungeon.level = null;
			Dungeon.branch = 0;
		}
	}

	private static void checkDragonCaveTreasure(DragonCaveLevel level, boolean firstVisit) {
		for (int cell = 0; cell < level.length(); cell++) {
			if (level.map[cell] != Terrain.SOKOBAN_HEAP) continue;
			Heap heap = level.heaps.get(cell);
			check(heap != null && heap.type == Heap.Type.SKELETON,
					"龙穴骸骨财宝缺失，格=" + cell);
			Item item = heap.peek();
			if (item instanceof Gold) {
				int quantity = item.quantity();
				check(firstVisit ? quantity >= 400 && quantity < 800
						: quantity >= 1 && quantity < 100,
						"龙穴金币数量不符合首次/重访规则，格=" + cell + "，数量=" + quantity);
			} else {
				check(firstVisit && item instanceof ScrollOfUpgrade,
						"龙穴重访生成了首次限定奖励，格=" + cell);
			}
		}
	}

	private static void testShadowEaterLevel() {
		Dungeon.depth = 17;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(16);
		ShadowEaterLevel level = new ShadowEaterLevel();
		level.transitions = new ArrayList<>();
		level.customTiles = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		level.blobs = new java.util.HashMap<>();
		level.traps = new SparseArray<>();
		check(level.build(), "暗噬避难所构建失败");
		level.buildFlagMaps();
		Dungeon.level = level;
		check(level.width() == 48 && level.height() == 48, "暗噬避难所必须为48x48");
		check(level.entrance() == ShadowEaterLevel.ENTRANCE, "暗噬避难所出生位置错误");
		for (int cell = 0; cell < level.length(); cell++) {
			check(level.map[cell] == TownLayouts.TOWN_LAYOUT[cell],
					"暗噬避难所固定地形不一致，格=" + cell);
		}
		check(level.customTiles.size() == 1
				&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
				"暗噬避难所原始城镇图层缺失");
		level.createItems();
		check(level.mobs().findMob(ShadowEaterLevel.PAINTER_POS) instanceof TownNpc
				&& ((TownNpc) level.mobs().findMob(ShadowEaterLevel.PAINTER_POS)).spec() == TownNpc.Spec.NUT_PAINTER,
				"暗噬避难所坚果教教主缺失或坐标错误");
		Dungeon.level = null;
		Dungeon.branch = 0;
	}

	private static void testPotLevel() {
		Dungeon.depth = 14;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(15);
		PotLevel level = new PotLevel();
		level.transitions = new ArrayList<>();
		level.customTiles = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		level.blobs = new java.util.HashMap<>();
		level.traps = new SparseArray<>();
		check(level.build(), "蜂蜜陶罐房构建失败");
		level.buildFlagMaps();
		Dungeon.level = level;
		check(level.width() == 48 && level.height() == 48, "蜂蜜陶罐房必须为48x48");
		check(level.entrance() == PotLevel.ENTRANCE, "蜂蜜陶罐房出生位置错误");
		check(level.map[PotLevel.ENTRANCE] == Terrain.TENT,
				"蜂蜜陶罐房必须保留旧版帐篷出生格");
		check(level.map[PotLevel.LEGACY_EXIT] == Terrain.WALL,
				"蜂蜜陶罐房旧版虚拟出口位置错误");
		for (int cell = 0; cell < level.length(); cell++) {
			check(level.map[cell] == SaveRoomLayouts.SAFE_ROOM_DEFAULT[cell],
					"蜂蜜陶罐房固定地形不一致，格=" + cell);
		}
		check(level.customTiles.size() == 1
				&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
				"蜂蜜陶罐房原始地砖图层缺失");
		Dungeon.level = null;
		Dungeon.branch = 0;
	}

	private static void testThiefBossLevel() {
		Dungeon.depth = 12;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(13);
		for (int seed = 0; seed < 250; seed++) {
			Random.pushGenerator(0x54484945464B0000L + seed);
			try {
				Dungeon.level = null;
				ThiefBossLevel level = new ThiefBossLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.mobs().clear();
				level.heaps = new SparseArray<>();
				level.blobs = new java.util.HashMap<>();
				level.traps = new SparseArray<>();
				check(level.build(), "盗贼王据点构建失败，种子=" + seed);
				level.buildFlagMaps();
				Dungeon.level = level;
				check(level.width() == 48 && level.height() == 48,
						"盗贼王据点必须为48x48，种子=" + seed);
				int entrance = level.entrance();
				int ex = entrance % level.width();
				int ey = entrance / level.width();
				check(level.map[entrance] == Terrain.PEDESTAL
						&& ex >= ThiefBossLevel.LEFT && ex < ThiefBossLevel.LEFT + ThiefBossLevel.HALL_WIDTH - 2
						&& ey >= ThiefBossLevel.TOP + ThiefBossLevel.HALL_HEIGHT + 2,
						"盗贼王据点入口位置错误，种子=" + seed);
				int door = level.arenaDoorForTesting();
				check(level.map[door] == Terrain.DOOR,
						"盗贼王据点竞技场门错误，种子=" + seed);
				boolean[] access = reachable(level, entrance, true);
				check(access[door] && access[(ThiefBossLevel.TOP + 1) * 48 + ThiefBossLevel.CENTER],
						"盗贼王据点入口无法到达竞技场，种子=" + seed);
			} finally {
				Random.popGenerator();
			}
		}
		ThiefKing boss = new ThiefKing();
		check(boss.HT == 2000 && boss.defenseSkill == 28 && boss.EXP == 60
				&& boss.attackSkill(null) == 25,
				"盗贼王旧版基础数值错误");
		Dungeon.level = null;
		Dungeon.branch = 0;
	}

	private static void testSkeletonAndCrabBossLevels() {
		for (int variant = 0; variant < 2; variant++) {
			Dungeon.depth = variant == 0 ? 8 : 10;
			Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(11 + variant);
			for (int seed = 0; seed < 250; seed++) {
				Random.pushGenerator(0x534B454C43520000L + variant * 1000L + seed);
				try {
					Dungeon.level = null;
					Level level = variant == 0 ? new SkeletonBossLevel() : new CrabBossLevel();
					level.transitions = new ArrayList<>(); level.customTiles = new ArrayList<>();
					level.mobs().clear(); level.heaps = new SparseArray<>();
					level.blobs = new java.util.HashMap<>(); level.traps = new SparseArray<>();
					boolean built = variant == 0 ? ((SkeletonBossLevel)level).build() : ((CrabBossLevel)level).build();
					check(built && level.width() == 48 && level.height() == 48,
							(variant == 0 ? "骷髅王陵" : "巨蟹王巢") + "必须成功构建为48x48");
					level.buildFlagMaps(); Dungeon.level = level;
					int door = variant == 0 ? ((SkeletonBossLevel)level).arenaDoorForTesting() : ((CrabBossLevel)level).arenaDoorForTesting();
					check(level.map[level.entrance()] == Terrain.PEDESTAL && level.map[door] == Terrain.DOOR,
							"独立首领长廊入口或南门错误，种子=" + seed);
					if (variant == 1) {
						check(Assets.Environment.TILES_PRISON.equals(level.tilesTex())
								&& Assets.Environment.WATER_PRISON.equals(level.waterTex()),
								"巨蟹王巢必须使用旧版监狱地砖和水面");
						int statues = 0;
						for (int cell : level.map) if (cell == Terrain.STATUE_SP) statues++;
						check(statues == 14, "巨蟹王巢长廊必须有14座旧版特殊雕像");
						int hallCell = (CrabBossLevel.TOP + 2) * 48 + CrabBossLevel.CENTER;
						check(level.map[hallCell] == Terrain.EMPTY || level.map[hallCell] == Terrain.EMPTY_DECO,
								"巨蟹王巢长廊被错误绘制成不可行走水域");
					}
					boolean[] access = reachable(level, level.entrance(), true);
					check(access[door] && access[(SkeletonBossLevel.TOP + 1) * 48 + SkeletonBossLevel.CENTER],
							"独立首领长廊不可达，种子=" + seed);
				} finally { Random.popGenerator(); }
			}
		}
		check(new SkeletonKing().HT == 2000 && new SkeletonKing().defenseSkill == 30
				&& new SkeletonHand1().HT == 1000 && new SkeletonHand2().HT == 1000,
				"骷髅王或双手旧版生命值错误");
		check(new CrabKing().HT == 1300 && new CrabKing().speed() == 2f
				&& new Shell().HT == 500 && new SpsHermitCrab().HT == 200,
				"巨蟹王、高压电壳或寄居蟹旧版生命值错误");
		check(new CrabKing().properties().contains(pd.actors.Char.Property.FISHER),
				"巨蟹王缺少旧版水生属性");
		check(new SpsHermitCrab().properties().contains(pd.actors.Char.Property.BEAST),
				"寄居蟹缺少旧版野兽属性");
		GoldenSkeletonKey masterKey = new GoldenSkeletonKey();
		check(masterKey.depth == 0 && masterKey.value() == 100
				&& masterKey.image == SpecificPlaceHolderDict.SOMETHING_0,
				"寄居蟹必掉水晶钥匙的属性或原始图标错误");
		testGoldenSkeletonKeyConsumption();
		testHermitCrabGuaranteedKeyDrop();
		Dungeon.level = null; Dungeon.branch = 0;
	}

	private static void testGoldenSkeletonKeyConsumption() {
		Actor.clear();
		Notes.reset();
		Dungeon.depth = 10;
		Dungeon.branch = 0;
		CrabBossLevel level = buildCrabBossLevelForInteraction();
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.pos = level.entrance();
		Dungeon.hero = hero;
		Actor.add(hero);

		Notes.add(new GoldenKey(Dungeon.depth));
		Notes.add(new GoldenSkeletonKey(0));
		int chestCell = hero.pos + 1;
		RecordingHeap lockedChest = putRecordingChest(level, chestCell, Heap.Type.LOCKED_CHEST);
		openChest(hero, chestCell);
		check(lockedChest.opened, "英雄未真实打开金锁宝箱");
		check(Notes.keyCount(new GoldenKey(Dungeon.depth)) == 0,
				"金锁宝箱必须优先消耗本层金钥匙");
		check(Notes.keyCount(new GoldenSkeletonKey(0)) == 1,
				"存在普通金钥匙时不应消耗水晶万能钥匙");

		Notes.add(new GoldenKey(Dungeon.depth));
		int legacyCrystalCell = hero.pos + level.width();
		RecordingHeap legacyCrystalChest = putRecordingChest(level, legacyCrystalCell, Heap.Type.CRYSTAL_CHEST);
		openChest(hero, legacyCrystalCell);
		check(legacyCrystalChest.opened && Notes.keyCount(new GoldenKey(Dungeon.depth)) == 0,
				"旧版宝库的黄金钥匙必须能真实打开水晶箱并被消耗");

		Dungeon.branch = 99;
		int branchChestCell = hero.pos - 1;
		RecordingHeap branchChest = putRecordingChest(level, branchChestCell, Heap.Type.LOCKED_CHEST);
		openChest(hero, branchChestCell);
		check(branchChest.opened && Notes.keyCount(new GoldenSkeletonKey(0)) == 0,
				"水晶万能钥匙必须能在支线真实打开金锁宝箱并被消耗");

		Notes.add(new GoldenSkeletonKey(0));
		int crystalChestCell = hero.pos + level.width();
		RecordingHeap crystalChest = putRecordingChest(level, crystalChestCell, Heap.Type.CRYSTAL_CHEST);
		openChest(hero, crystalChestCell);
		check(crystalChest.opened && Notes.keyCount(new GoldenSkeletonKey(0)) == 0,
				"水晶万能钥匙必须能在支线真实打开水晶宝箱并被消耗");
	}

	private static void testHermitCrabGuaranteedKeyDrop() {
		Actor.clear();
		Notes.reset();
		Dungeon.depth = 10;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(12);
		CrabBossLevel level = buildCrabBossLevelForInteraction();
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.lvl = Hero.MAX_LEVEL + 3;
		hero.pos = level.entrance();
		Dungeon.hero = hero;
		Actor.add(hero);

		SpsHermitCrab crab = new SpsHermitCrab();
		crab.pos = hero.pos + level.width();
		crab.sprite = new SilentCharSprite();
		level.mobs().add(crab);
		Actor.add(crab);
		crab.die(crab);

		Heap heap = level.heaps.get(crab.pos);
		check(heap != null && heap.peek() instanceof GoldenSkeletonKey,
				"寄居蟹死亡后必须在原地真实掉落水晶万能钥匙");
		check(((GoldenSkeletonKey) heap.peek()).depth == 0,
				"寄居蟹掉落的水晶万能钥匙必须可跨深度使用");
		check(!level.mobs().contains(crab), "寄居蟹死亡后未从地图演员列表移除");
	}

	private static CrabBossLevel buildCrabBossLevelForInteraction() {
		CrabBossLevel level = new InteractionCrabBossLevel();
		level.transitions = new ArrayList<>();
		level.customTiles = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		level.blobs = new java.util.HashMap<>();
		level.traps = new SparseArray<>();
		check(level.build(), "巨蟹王巢交互测试地图构建失败");
		level.buildFlagMaps();
		Arrays.fill(level.heroFOV, true);
		Dungeon.level = level;
		return level;
	}

	private static RecordingHeap putRecordingChest(Level level, int cell, Heap.Type type) {
		RecordingHeap heap = new RecordingHeap();
		heap.pos = cell;
		heap.type = type;
		heap.drop(new Gold(1));
		level.heaps.put(cell, heap);
		return heap;
	}

	private static void openChest(Hero hero, int cell) {
		hero.curAction = new HeroAction.OpenChest(cell);
		hero.ready = true;
		hero.onOperateComplete();
	}

	private static void testTenguDenLevel() {
		Dungeon.depth = 6;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(10);
		pd.actors.hero.Hero previousHero = Dungeon.hero;
		Dungeon.hero = new pd.actors.hero.Hero();
		for (int seed = 0; seed < 250; seed++) {
			Random.pushGenerator(0x54454E4755440000L + seed);
			try {
				pd.actors.Actor.clear();
				Dungeon.level = null;
				TenguDenLevel level = new TenguDenLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.mobs().clear();
				level.heaps = new SparseArray<>();
				level.blobs = new java.util.HashMap<>();
				level.traps = new SparseArray<>();
				check(level.build(), "天狗隐匿处构建失败，种子=" + seed);
				level.buildFlagMaps();
				Dungeon.level = level;
				check(level.width() == 48 && level.height() == 48,
						"天狗隐匿处必须为48x48，种子=" + seed);
				check(level.legacyRoomCount() >= 20 && level.legacyGenerationAttempts() <= 64,
						"天狗隐匿处BSP结构或重试上限错误，种子=" + seed);
				check(level.map[level.entrance()] == Terrain.PEDESTAL,
						"天狗隐匿处入口必须是旧版基座，种子=" + seed);
				int hiddenDoors = 0;
				for (int terrain : level.map) if (terrain == Terrain.SECRET_DOOR) hiddenDoors++;
				check(hiddenDoors >= 1 && level.heaps.valueList().size() >= 4,
						"天狗宝箱密室的隐藏门或宝箱环缺失，种子=" + seed
								+ "，隐藏门=" + hiddenDoors + "，宝箱=" + level.heaps.valueList().size());
				check(level.passable[level.bossCellForTesting()],
						"匿藏天狗生成点不可用，种子=" + seed);
				boolean[] access = reachable(level, level.entrance(), true);
				check(access[level.bossCellForTesting()],
						"入口无法到达匿藏天狗，种子=" + seed);
				level.createMobs();
				check(countMobs(level, TenguDen.class) == 1
						&& countMobs(level, TownNpc.class) == 1,
						"天狗隐匿处必须生成匿藏天狗与宝藏猎人，种子=" + seed);
				for (Mob mob : level.mobs()) {
					if (mob instanceof TownNpc) {
						check(((TownNpc)mob).spec() == TownNpc.Spec.STORM_AND_RAIN,
								"天狗宝箱密室NPC身份错误，种子=" + seed);
					}
				}
			} finally {
				Random.popGenerator();
			}
		}
		TenguDen boss = new TenguDen();
		check(boss.HT == 2000 && boss.defenseSkill == 30 && boss.EXP == 20
				&& boss.attackSkill(null) == 28 && boss.speed() == 2f,
				"匿藏天狗旧版基础数值错误");
		Dungeon.level = null;
		Dungeon.hero = previousHero;
		Dungeon.branch = 0;
	}

	private static void testInfestBossLevel() {
		Dungeon.depth = 4;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(9);
		for (int seed = 0; seed < 250; seed++) {
			Random.pushGenerator(0x494E464553540000L + seed);
			try {
				pd.actors.Actor.clear();
				InfestBossLevel level = new InfestBossLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.mobs().clear();
				level.heaps = new SparseArray<>();
				level.blobs = new java.util.HashMap<>();
				level.traps = new SparseArray<>();
				check(level.build(), "寄生虫巢构建失败，种子=" + seed);
				level.buildFlagMaps();
				Dungeon.level = level;
				check(level.width() == 48 && level.height() == 48,
						"寄生虫巢必须为48x48，种子=" + seed);
				int entrance = level.entrance();
				int x = entrance % level.width();
				int y = entrance / level.width();
				check(x > InfestBossLevel.ROOM_LEFT && x < InfestBossLevel.ROOM_RIGHT
						&& y > InfestBossLevel.ROOM_TOP && y < InfestBossLevel.ROOM_BOTTOM,
						"寄生虫巢入口不在中央房，种子=" + seed);
				check(level.map[entrance] == Terrain.PEDESTAL,
						"寄生虫巢入口必须是旧版基座，种子=" + seed);
				boolean[] access = reachable(level, entrance, true);
				int reachable = 0;
				for (boolean value : access) if (value) reachable++;
				check(reachable > 250, "寄生虫巢战区不可达，种子=" + seed);
				for (pd.levels.traps.Trap trap : level.traps.valueList()) {
					check(level.map[trap.pos] == Terrain.TRAP,
							"寄生虫巢残留墙内陷阱，种子=" + seed + "，格=" + trap.pos);
				}
				level.createMobs();
				check(level.mobs().size() == InfestBossLevel.INITIAL_MOB_COUNT,
						"寄生虫巢必须生成20个常驻怪，种子=" + seed);
				for (Mob mob : level.mobs()) {
					check(mob instanceof GoldOrc || mob instanceof Fiend,
							"寄生虫巢常驻怪类型错误，种子=" + seed);
				}
			} finally {
				Random.popGenerator();
			}
		}
		GoldOrc orc = new GoldOrc();
		check(orc.HT == 500 && orc.defenseSkill == 35 && orc.EXP == 25
				&& orc.attackSkill(null) == 55 && orc.attackDelay() == 1.5f,
				"金眼猪人旧版基础数值错误");
		Fiend fiend = new Fiend();
		check(fiend.HT >= 150 && fiend.HT <= 255 && fiend.defenseSkill == 2
				&& fiend.EXP == 20 && fiend.attackSkill(null) == 50 && fiend.speed() == 1.5f,
				"邪魔旧版基础数值错误");
		ShadowYog yog = new ShadowYog();
		check(yog.HT == 50 && yog.defenseSkill == 32 && yog.EXP == 100
				&& yog.attackSkill(null) == 50 && yog.speed() == 2f && yog.drRoll() == 25,
				"Yog之影旧版基础数值错误");
		Dungeon.level = null;
		Dungeon.branch = 0;
	}

	private static void testCavesBossLevel() {
		Dungeon.depth = 15;
		Dungeon.branch = 0;
		HashSet<Integer> variants = new HashSet<>();
		for (int seed = 0; seed < 2500; seed++) {
			Random.pushGenerator(0x4341564553420000L + seed);
			try {
				SpsCavesBossLevel level = new SpsCavesBossLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.mobs().clear();
				level.heaps = new SparseArray<>();
				level.blobs = new java.util.HashMap<>();
				level.traps = new SparseArray<>();
				check(level.build(), "洞穴首领地图构建失败，种子=" + seed);
				level.buildFlagMaps();
				Dungeon.level = level;
				SpsLegacyLevelVisual legacyVisual = checkLegacyVisual(level,
						Assets.Environment.SPS_TILES_CAVES_LEGACY,
						Assets.Environment.SPS_WATER_CAVES, "洞穴首领", seed);

				check(level.width() == 48 && level.height() == 48,
						"洞穴首领地图必须为48x48，种子=" + seed);
				int entrance = level.entrance();
				int exit = level.exit();
				int ex = entrance % level.width();
				int ey = entrance / level.width();
				check(ex > SpsCavesBossLevel.ROOM_LEFT && ex < SpsCavesBossLevel.ROOM_RIGHT
						&& ey > SpsCavesBossLevel.ROOM_TOP && ey < SpsCavesBossLevel.ROOM_BOTTOM,
						"洞穴首领入口不在中央房，种子=" + seed);
				check(level.map[entrance] == Terrain.ENTRANCE
						&& level.map[exit] == Terrain.LOCKED_EXIT,
						"洞穴首领入口或锁闭出口错误，种子=" + seed);
				int arenaDoor = level.arenaDoorForTesting();
				check(arenaDoor / level.width() == SpsCavesBossLevel.ROOM_BOTTOM + 1
						&& arenaDoor % level.width() >= SpsCavesBossLevel.ROOM_LEFT
						&& arenaDoor % level.width() < SpsCavesBossLevel.ROOM_RIGHT
						&& level.map[arenaDoor] == Terrain.DOOR,
						"洞穴首领南门位置错误，种子=" + seed);

				boolean[] access = reachable(level, entrance, true);
				check(access[arenaDoor] && access[exit],
						"洞穴首领入口无法连通竞技场或出口，种子=" + seed);
				boolean reachedOutside = false;
				for (int cell = 0; cell < access.length; cell++) {
					if (access[cell] && level.outsideEntranceRoom(cell)) { reachedOutside = true; break; }
				}
				check(reachedOutside, "洞穴首领中央房无法离开，种子=" + seed);
				for (pd.levels.traps.Trap trap : level.traps.valueList()) {
					check(level.map[trap.pos] == Terrain.INACTIVE_TRAP,
							"洞穴首领地图残留墙内陷阱，种子=" + seed + "，格=" + trap.pos);
					check(!trap.active, "洞穴首领旧陷阱不应处于启用状态，种子=" + seed);
				}

				int selected = level.bossVariantForTesting();
				variants.add(selected);
				check(selected == level.bossVariantForTesting(), "洞穴首领选择被重复抽取");
				Mob boss = level.createLegacyBoss();
				check((selected == SpsCavesBossLevel.HYBRID && boss instanceof Hybrid)
						|| (selected == SpsCavesBossLevel.DM300 && boss instanceof SpsDM300)
						|| (selected == SpsCavesBossLevel.SPIDER_QUEEN && boss instanceof SpiderQueen),
						"洞穴首领选择与生成类型不一致，种子=" + seed);
				if (seed == 0) {
					Level.set(arenaDoor, Terrain.WALL, level);
					Level.set(entrance, Terrain.WALL_DECO, level);
					checkLegacyCell(level, legacyVisual, arenaDoor, Terrain.WALL, "洞穴首领封闭竞技场门");
					checkLegacyCell(level, legacyVisual, entrance, Terrain.WALL_DECO, "洞穴首领封闭入口");
					Level.set(arenaDoor, Terrain.EMPTY_DECO, level);
					Level.set(entrance, Terrain.ENTRANCE, level);
					Level.set(exit, Terrain.EXIT, level);
					checkLegacyCell(level, legacyVisual, arenaDoor, Terrain.EMPTY_DECO, "洞穴首领开启竞技场门");
					checkLegacyCell(level, legacyVisual, entrance, Terrain.ENTRANCE, "洞穴首领恢复入口");
					checkLegacyCell(level, legacyVisual, exit, Terrain.EXIT, "洞穴首领开启出口");
					checkRestoredLegacyVisual(level, new SpsCavesBossLevel(),
							Assets.Environment.SPS_TILES_CAVES_LEGACY,
							Assets.Environment.SPS_WATER_CAVES, "洞穴首领");
				}
			} finally {
				Random.popGenerator();
			}
		}
		check(variants.size() == 3, "2500个确定种子没有覆盖全部三种洞穴首领");

		Hybrid hybrid = new Hybrid();
		check(hybrid.HT == 800 && hybrid.defenseSkill == 10 && hybrid.EXP == 50
				&& hybrid.attackSkill(null) == 50 && hybrid.speed() == 1.5f,
				"混源体旧版基础数值错误");
		SpsDM300 dm300 = new SpsDM300();
		check(dm300.HT == 800 && dm300.defenseSkill == 24 && dm300.EXP == 50
				&& dm300.attackSkill(null) == 35,
				"SPS DM-300旧版基础数值错误");
		SpiderQueen queen = new SpiderQueen();
		check(queen.HT == 1000 && queen.defenseSkill == 35 && queen.EXP == 50
				&& queen.attackSkill(null) == 40 && queen.speed() == 0.8f,
				"蜘蛛皇后旧版基础数值错误");
		check(new SpiderQueen.SpiderEgg().HT == 25
				&& new SpiderQueen.SpiderWorker().HT == 150
				&& new SpiderQueen.SpiderMind().HT == 100
				&& new SpiderQueen.SpiderJumper().HT == 150
				&& new SpiderQueen.SpiderGold().HT == 300,
				"蜘蛛皇后孵化物旧版生命值错误");
		Dungeon.depth = 1;
		Dungeon.level = null;
	}

	private static void testCityBossGeometry() {
		Dungeon.depth = 20;
		Dungeon.branch = 0;
		HashSet<Integer> variants = new HashSet<>();
		for (int seed = 0; seed < 2500; seed++) {
			Random.pushGenerator(0x43495459424F0000L + seed);
			try {
				SpsCityBossLevel level = new SpsCityBossLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.mobs().clear();
				level.heaps = new SparseArray<>();
				level.blobs = new java.util.HashMap<>();
				level.traps = new SparseArray<>();
				check(level.build(), "矮人城首领旧地图构建失败，种子=" + seed);
				level.buildFlagMaps();
				Dungeon.level = level;
				SpsLegacyLevelVisual legacyVisual = checkLegacyVisual(level,
						Assets.Environment.SPS_TILES_CITY_LEGACY,
						Assets.Environment.SPS_WATER_CITY, "矮人城首领", seed);
				check(level.width() == 48 && level.height() == 48, "矮人城首领地图必须为48x48");
				check(level.exit() == SpsCityBossLevel.EXIT
						&& level.map[SpsCityBossLevel.EXIT] == Terrain.LOCKED_EXIT,
						"矮人城首领出口位置错误");
				check(level.map[SpsCityBossLevel.ARENA_DOOR] == Terrain.DOOR,
						"矮人城首领南门位置错误");
				check(level.map[SpsCityBossLevel.WELL] == Terrain.WELL,
						"矮人城首领中央井位置错误");
				check(level.map[SpsCityBossLevel.pedestal(true)] == Terrain.PEDESTAL
						&& level.map[SpsCityBossLevel.pedestal(false)] == Terrain.PEDESTAL,
						"矮人城首领双王座位置错误");
				check(level.entrance() / level.width() >= 19 && level.entrance() / level.width() <= 20,
						"矮人城首领入口行错误");
				boolean[] access = reachable(level, level.entrance(), true);
				check(access[SpsCityBossLevel.ARENA_DOOR]
						&& access[SpsCityBossLevel.pedestal(true)]
						&& access[SpsCityBossLevel.pedestal(false)]
						&& access[SpsCityBossLevel.EXIT],
						"矮人城首领长廊不连通，种子=" + seed);
				int selected = level.bossVariantForTesting();
				variants.add(selected);
				check(selected == level.bossVariantForTesting(), "矮人城首领选择被重复抽取");
				Mob boss = level.createLegacyBoss();
				check((selected == SpsCityBossLevel.LICH_DANCER && boss instanceof LichDancer)
						|| (selected == SpsCityBossLevel.ELDER_AVATAR && boss instanceof ElderAvatar)
						|| (selected == SpsCityBossLevel.KING && boss instanceof King),
						"矮人城首领选择与生成类型不一致，种子=" + seed);
				if (seed == 0) {
					int entrance = level.entrance();
					Level.set(SpsCityBossLevel.ARENA_DOOR, Terrain.WALL, level);
					Level.set(entrance, Terrain.WALL_DECO, level);
					checkLegacyCell(level, legacyVisual, SpsCityBossLevel.ARENA_DOOR,
							Terrain.WALL, "矮人城首领封闭竞技场门");
					checkLegacyCell(level, legacyVisual, entrance, Terrain.WALL_DECO, "矮人城首领封闭入口");
					Level.set(SpsCityBossLevel.ARENA_DOOR, Terrain.EMPTY_DECO, level);
					Level.set(entrance, Terrain.ENTRANCE, level);
					Level.set(SpsCityBossLevel.EXIT, Terrain.EXIT, level);
					checkLegacyCell(level, legacyVisual, SpsCityBossLevel.ARENA_DOOR,
							Terrain.EMPTY_DECO, "矮人城首领开启竞技场门");
					checkLegacyCell(level, legacyVisual, entrance, Terrain.ENTRANCE, "矮人城首领恢复入口");
					checkLegacyCell(level, legacyVisual, SpsCityBossLevel.EXIT, Terrain.EXIT, "矮人城首领开启出口");
					checkRestoredLegacyVisual(level, new SpsCityBossLevel(),
							Assets.Environment.SPS_TILES_CITY_LEGACY,
							Assets.Environment.SPS_WATER_CITY, "矮人城首领");
				}
			} finally {
				Random.popGenerator();
			}
		}
		check(variants.size() == 3, "2500个确定种子没有覆盖全部三种矮人城首领");
		LichDancer lich = new LichDancer();
		check(lich.HT == 1000 && lich.defenseSkill == 25 && lich.EXP == 60
				&& lich.attackSkill(null) == 65, "巫妖舞者旧版基础数值错误");
		check(new LichDancer.BatteryTomb().HT == 200, "死灵电池旧版生命值错误");
		ElderAvatar elder = new ElderAvatar();
		check(elder.HT == 600 && elder.defenseSkill == 25 && elder.EXP == 50
				&& elder.attackSkill(null) == 58, "长老化身旧版基础数值错误");
		check(new ElderAvatar.Obelisk().HT == 1000
				&& new ElderAvatar.TheHunter().HT == 100
				&& new ElderAvatar.TheWarlock().HT == 150
				&& new ElderAvatar.TheMonk().HT == 100
				&& new ElderAvatar.TheMech().HT == 200,
				"长老化身尖碑或四类援军生命值错误");
		King king = new King();
		check(king.HT == 1500 && king.defenseSkill == 25 && king.EXP == 60
				&& king.attackSkill(null) == 62,
				"矮人国王旧版基础数值错误");
		check(new King.Undead().HT == 100 && new King.DwarfKingTomb().HT == 1000,
				"矮人国王亡灵或永恒之墓生命值错误");
		Dungeon.depth = 1;
		Dungeon.level = null;
	}

	private static void testHallsBossLevel() {
		Dungeon.depth = 25;
		Dungeon.branch = 0;
		for (int seed = 0; seed < 2500; seed++) {
			Random.pushGenerator(0x48414C4C53420000L + seed);
			try {
				SpsHallsBossLevel level = new SpsHallsBossLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.mobs().clear();
				level.heaps = new SparseArray<>();
				level.blobs = new java.util.HashMap<>();
				level.traps = new SparseArray<>();
				check(level.build(), "恶魔大厅首领地图构建失败，种子=" + seed);
				level.buildFlagMaps();
				Dungeon.level = level;
				SpsLegacyLevelVisual legacyVisual = checkLegacyVisual(level,
						Assets.Environment.SPS_TILES_HALLS_LEGACY,
						Assets.Environment.SPS_WATER_HALLS, "恶魔大厅首领", seed);
				check(level.width() == 48 && level.height() == 48, "恶魔大厅首领地图必须为48x48");
				check(level.entrance() == SpsHallsBossLevel.ENTRANCE
						&& level.map[level.entrance()] == Terrain.ENTRANCE,
						"恶魔大厅首领入口坐标错误，种子=" + seed);
				check(level.map[level.exit()] == Terrain.LOCKED_EXIT,
						"恶魔大厅首领出口必须锁闭，种子=" + seed);
				int westGate = (SpsHallsBossLevel.ROOM_LEFT - 1) + 24 * level.width();
				int westHall = westGate - 1;
				check(level.map[westGate] == Terrain.WALL && (Terrain.flags[level.map[westHall]] & Terrain.PASSABLE) != 0,
						"恶魔大厅中央房西侧开启路径错误，种子=" + seed);
				level.map[westGate] = Terrain.EMPTY_SP;
				level.buildFlagMaps();
				boolean[] access = reachable(level, level.entrance(), true);
				check(access[level.exit()], "恶魔大厅开启中央房后无法到达出口，种子=" + seed);
				if (seed == 0) {
					level.map[westGate] = Terrain.WALL;
					level.buildFlagMaps();
					Level.set(SpsHallsBossLevel.ENTRANCE, Terrain.WALL_DECO, level);
					checkLegacyCell(level, legacyVisual, SpsHallsBossLevel.ENTRANCE,
							Terrain.WALL_DECO, "恶魔大厅封闭入口");
					for (int x = SpsHallsBossLevel.ROOM_LEFT - 1; x <= SpsHallsBossLevel.ROOM_RIGHT + 1; x++) {
						Level.set((SpsHallsBossLevel.ROOM_TOP - 1) * level.width() + x,
								Terrain.EMPTY_SP, level);
						Level.set((SpsHallsBossLevel.ROOM_BOTTOM + 1) * level.width() + x,
								Terrain.EMPTY_SP, level);
						checkLegacyCell(level, legacyVisual,
								(SpsHallsBossLevel.ROOM_TOP - 1) * level.width() + x,
								Terrain.EMPTY_SP, "恶魔大厅开启中央房北墙");
						checkLegacyCell(level, legacyVisual,
								(SpsHallsBossLevel.ROOM_BOTTOM + 1) * level.width() + x,
								Terrain.EMPTY_SP, "恶魔大厅开启中央房南墙");
					}
					for (int y = SpsHallsBossLevel.ROOM_TOP; y <= SpsHallsBossLevel.ROOM_BOTTOM; y++) {
						Level.set(y * level.width() + SpsHallsBossLevel.ROOM_LEFT - 1,
								Terrain.EMPTY_SP, level);
						Level.set(y * level.width() + SpsHallsBossLevel.ROOM_RIGHT + 1,
								Terrain.EMPTY_SP, level);
						checkLegacyCell(level, legacyVisual,
								y * level.width() + SpsHallsBossLevel.ROOM_LEFT - 1,
								Terrain.EMPTY_SP, "恶魔大厅开启中央房西墙");
						checkLegacyCell(level, legacyVisual,
								y * level.width() + SpsHallsBossLevel.ROOM_RIGHT + 1,
								Terrain.EMPTY_SP, "恶魔大厅开启中央房东墙");
					}
					Level.set(SpsHallsBossLevel.ENTRANCE, Terrain.ENTRANCE, level);
					Level.set(level.exit(), Terrain.EXIT, level);
					checkLegacyCell(level, legacyVisual, SpsHallsBossLevel.ENTRANCE,
							Terrain.ENTRANCE, "恶魔大厅恢复入口");
					checkLegacyCell(level, legacyVisual, level.exit(), Terrain.EXIT, "恶魔大厅开启出口");
					checkRestoredLegacyVisual(level, new SpsHallsBossLevel(),
							Assets.Environment.SPS_TILES_HALLS_LEGACY,
							Assets.Environment.SPS_WATER_HALLS, "恶魔大厅首领");
				}
			} finally {
				Random.popGenerator();
			}
		}
		Yog yog = new Yog();
		check(yog.HT == 2000 && yog.EXP == 50 && yog.damageRoll() >= 54 && yog.damageRoll() <= 96,
				"Yog-Dzewa旧版基础数值错误");
		check(new Yog.RottingFist().HT == 1500
				&& new Yog.BurningFist().HT == 1000
				&& new Yog.InfectingFist().HT == 1500
				&& new Yog.PinningFist().HT == 1000
				&& new Yog.Larva().HT == 25,
				"Yog四种元素之拳或幼虫生命值错误");
		Dungeon.depth = 1;
		Dungeon.level = null;
	}

	private static void testMainBossLayouts() {
		check(SpsBossLayouts.SEWERS.length == 48 * 48, "下水道首领旧版模板必须为48x48");
		check(SpsBossLayouts.PRISON.length == 48 * 48, "监狱首领旧版模板必须为48x48");
		for (int chapter = 0; chapter < 2; chapter++) {
			Dungeon.depth = chapter == 0 ? 5 : 10;
			Dungeon.branch = 0;
			Random.pushGenerator(0x535053424F53534CL + chapter);
			try {
				Level level = chapter == 0 ? new SpsSewerBossLevel() : new SpsPrisonBossLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.customTerrain = new ArrayList<>();
				level.customWalls = new ArrayList<>();
				level.mobs().clear();
				level.heaps = new SparseArray<>();
				level.blobs = new java.util.HashMap<>();
				level.plants = new SparseArray<>();
				level.traps = new SparseArray<>();
				boolean built = chapter == 0
						? ((SpsSewerBossLevel) level).build()
						: ((SpsPrisonBossLevel) level).build();
				check(built, "主线旧版首领地图构建失败，章节=" + chapter);
				level.buildFlagMaps();
				check(level.width() == 48 && level.height() == 48,
						"主线旧版首领地图尺寸错误，章节=" + chapter);
				check(level.entrance() == SpsFixedBossLevel.ENTRANCE
						&& level.exit() == SpsFixedBossLevel.EXIT,
						"主线旧版首领地图入口出口错误，章节=" + chapter);
				check(level.map[SpsFixedBossLevel.ENTRANCE] == Terrain.ENTRANCE
						&& level.map[SpsFixedBossLevel.EXIT] == Terrain.LOCKED_EXIT,
						"主线旧版首领地图门禁地形错误，章节=" + chapter);
				check(level.passable[SpsFixedBossLevel.BOSS_CELL],
						"主线旧版首领中心生成点不可用，章节=" + chapter);
				SpsLegacyLevelVisual legacyVisual = null;
				for (pd.tiles.CustomTilemap tile : level.customTiles) {
					if (tile instanceof SpsLegacyLevelVisual) legacyVisual = (SpsLegacyLevelVisual) tile;
				}
				check(legacyVisual != null, "下水道/监狱首领图缺少旧版整图视觉，章节=" + chapter);
				for (int cell = 0; cell < level.length(); cell++) {
					int expected = SpsLegacyLevelVisual.terrainVisual(level.map[cell]);
					check(expected >= 0 && expected < 64,
							"下水道/监狱首领旧图索引越界，章节=" + chapter + "，cell=" + cell);
					check(legacyVisual.visualAt(cell) == expected,
							"下水道/监狱首领旧图不同步，章节=" + chapter + "，cell=" + cell);
				}
				Dungeon.level = level;
				level.seal();
				check(legacyVisual.visualAt(SpsFixedBossLevel.ENTRANCE) == 12,
						"首领封门视觉没有同步，章节=" + chapter);
				level.unseal();
				check(legacyVisual.visualAt(SpsFixedBossLevel.ENTRANCE) == 7
						&& legacyVisual.visualAt(SpsFixedBossLevel.EXIT) == 8,
						"首领解封视觉没有同步，章节=" + chapter);
				boolean[] access = reachable(level, SpsFixedBossLevel.ENTRANCE, true);
				check(access[SpsFixedBossLevel.BOSS_CELL],
						"主线旧版首领地图无法进入中心战区，章节=" + chapter);
			} finally {
				Random.popGenerator();
			}
		}
		Dungeon.depth = 1;
		SpsGoo goo = new SpsGoo();
		check(goo.HT == 350 && goo.defenseSkill == 12 && goo.EXP == 20
				&& goo.attackSkill(null) == 15,
				"SPS黏咕旧版基础数值错误");
		SpsGoo.PoisonGoo poisonGoo = new SpsGoo.PoisonGoo();
		check(poisonGoo.HT == 100 && poisonGoo.defenseSkill == 12
				&& poisonGoo.EXP == 1 && poisonGoo.attackSkill(null) == 5,
				"SPS毒性黏咕旧版基础数值错误");
		SewerHeart heart = new SewerHeart();
		check(heart.HT == 500 && heart.defenseSkill == 0 && heart.EXP == 30
				&& heart.attackSkill(null) == 30,
				"下水道之心旧版基础数值错误");
		SewerHeart.SewerLasher lasher = new SewerHeart.SewerLasher();
		check(lasher.HT == 60 && lasher.defenseSkill == 0 && lasher.EXP == 1
				&& lasher.attackSkill(null) == 15,
				"下水道藤鞭旧版基础数值错误");
		PlagueDoctor doctor = new PlagueDoctor();
		check(doctor.HT == 500 && doctor.defenseSkill == 5 && doctor.EXP == 30
				&& doctor.attackSkill(null) == 30 && doctor.speed() == 0.75f,
				"瘟疫医生旧版基础数值错误");
		PlagueDoctor.ShadowRat shadowRat = new PlagueDoctor.ShadowRat();
		check(shadowRat.HT == 60 && shadowRat.defenseSkill == 3
				&& shadowRat.EXP == 1 && shadowRat.attackSkill(null) == 25,
				"瘟疫之影旧版基础数值错误");
		check(heart.SupercreateLoot() instanceof MissileShield,
				"下水道之心特殊战利品必须为神木圆盾");
		check(doctor.SupercreateLoot() instanceof PotionOfMage,
				"瘟疫医生特殊战利品必须为奇迹烧瓶");
		check(new TestMob().SupercreateLoot() instanceof StoneOre,
				"无专属战利品的怪物必须按旧版抢夺出石矿");
		check(ClassSkill.createFor(HeroClass.ROGUE) instanceof RogueSkill,
				"盗贼职业技能映射错误");
		Dagger reinforced = new Dagger();
		reinforced.reinforce();
		Bundle reinforcedBundle = new Bundle();
		reinforced.storeInBundle(reinforcedBundle);
		Dagger restoredReinforced = new Dagger();
		restoredReinforced.restoreFromBundle(reinforcedBundle);
		check(restoredReinforced.isReinforced(), "精金破阶标记没有随存档恢复");
		MissileShield shield = new MissileShield();
		for (int i = 0; i < 15; i++) shield.gainCharge();
		check(shield.charge() == MissileShield.FULL_CHARGE, "神木圆盾充能没有限制在10点");
		Bundle shieldBundle = new Bundle();
		shield.storeInBundle(shieldBundle);
		MissileShield restoredShield = new MissileShield();
		restoredShield.restoreFromBundle(shieldBundle);
		check(restoredShield.charge() == shield.charge(), "神木圆盾充能存档恢复错误");
		PotionOfMage flask = new PotionOfMage();
		for (int i = 0; i < 120; i++) flask.gainCharge();
		check(flask.charge() == PotionOfMage.FULL_CHARGE, "奇迹烧瓶充能没有限制在100点");
		Bundle flaskBundle = new Bundle();
		flask.storeInBundle(flaskBundle);
		PotionOfMage restoredFlask = new PotionOfMage();
		restoredFlask.restoreFromBundle(flaskBundle);
		check(restoredFlask.charge() == flask.charge(), "奇迹烧瓶充能存档恢复错误");
		check(new Gleaf().curEnergy == 4 && new Dpotion().curEnergy == 4,
				"第7皮肤首领奖励必须各有4点初始能量");
		SpsTengu tengu = new SpsTengu();
		check(tengu.HT == 600 && tengu.defenseSkill == 30 && tengu.EXP == 40
				&& tengu.attackSkill(null) == 40,
				"SPS天狗旧版基础数值错误");
		PrisonWander warden = new PrisonWander();
		check(warden.HT == 800 && warden.defenseSkill == 20 && warden.EXP == 40
				&& warden.attackSkill(null) == 35 && warden.attackDelay() == 0.75f,
				"SPS典狱长旧版基础数值错误");
		Tank tank = new Tank();
		check(tank.HT == 1000 && tank.defenseSkill == 5 && tank.EXP == 40
				&& tank.attackSkill(null) == 60 && tank.attackDelay() == 3f,
				"SPS TANK旧版基础数值错误");

		HashSet<Integer> sewerVariants = new HashSet<>();
		for (int seed = 0; seed < 128 && sewerVariants.size() < 3; seed++) {
			Random.pushGenerator(0x53455745520000L + seed);
			try {
				SpsSewerBossLevel sewer = new SpsSewerBossLevel();
				sewerVariants.add(sewer.bossVariantForTesting());
				int selected = sewer.bossVariantForTesting();
				check(selected == sewer.bossVariantForTesting(), "下水道首领选择被重复抽取");
				Mob boss = sewer.createLegacyBoss();
				check((selected == SpsSewerBossLevel.GOO && boss instanceof SpsGoo)
						|| (selected == SpsSewerBossLevel.SEWER_HEART && boss instanceof SewerHeart)
						|| (selected == SpsSewerBossLevel.PLAGUE_DOCTOR && boss instanceof PlagueDoctor),
						"下水道首领选择与生成类型不一致");
			} finally {
				Random.popGenerator();
			}
		}
		check(sewerVariants.size() == 3, "128个确定种子没有覆盖全部三种下水道首领");

		HashSet<Integer> variants = new HashSet<>();
		for (int seed = 0; seed < 128 && variants.size() < 3; seed++) {
			Random.pushGenerator(0x505249534F4E0000L + seed);
			try {
				SpsPrisonBossLevel prison = new SpsPrisonBossLevel();
				variants.add(prison.bossVariantForTesting());
				int selected = prison.bossVariantForTesting();
				check(selected == prison.bossVariantForTesting(), "监狱首领选择被重复抽取");
				Mob boss = prison.createLegacyBoss();
				check((selected == SpsPrisonBossLevel.TENGU && boss instanceof SpsTengu)
						|| (selected == SpsPrisonBossLevel.PRISON_WANDER && boss instanceof PrisonWander)
						|| (selected == SpsPrisonBossLevel.TANK && boss instanceof Tank),
						"监狱首领选择与生成类型不一致");
			} finally {
				Random.popGenerator();
			}
		}
		check(variants.size() == 3, "128个确定种子没有覆盖全部三种监狱首领");
	}

	private static void testLegacyBossProperties() {
		Dungeon.depth = 15;
		assertProperties(new SpsGoo(), Char.Property.UNKNOW, Char.Property.BOSS);
		assertProperties(new SpsGoo.PoisonGoo(), Char.Property.ELEMENT, Char.Property.MINIBOSS);
		assertProperties(new SewerHeart(), Char.Property.PLANT, Char.Property.BOSS);
		assertProperties(new SewerHeart.SewerLasher(), Char.Property.PLANT, Char.Property.MINIBOSS);
		assertProperties(new PlagueDoctor(), Char.Property.HUMAN, Char.Property.BOSS);
		assertProperties(new PlagueDoctor.ShadowRat(), Char.Property.UNKNOW, Char.Property.MINIBOSS);
		assertProperties(new SpsTengu(), Char.Property.HUMAN, Char.Property.BOSS);
		assertProperties(new PrisonWander(), Char.Property.HUMAN, Char.Property.BOSS);
		assertProperties(new PrisonWander.SeekBombP(), Char.Property.MECH, Char.Property.MINIBOSS);
		assertProperties(new Tank(), Char.Property.UNDEAD, Char.Property.BOSS);

		assertProperties(new Hybrid(), Char.Property.BEAST, Char.Property.ALIEN,
				Char.Property.UNDEAD, Char.Property.MECH, Char.Property.BOSS);
		assertProperties(new Hybrid.Mixers(), Char.Property.UNKNOW, Char.Property.BOSS);
		assertProperties(new SpsDM300(), Char.Property.MECH, Char.Property.BOSS);
		SpsDM300.Tower tower = new SpsDM300.Tower();
		assertProperties(tower, Char.Property.MECH, Char.Property.BOSS);
		check(tower.resist(pd.actors.blobs.Electricity.class) == 0.5f
				&& tower.isImmune(pd.actors.blobs.ConfusionGas.class)
				&& tower.isImmune(pd.actors.blobs.ToxicGas.class)
				&& tower.isImmune(pd.actors.buffs.Terror.class),
				"DM-300炮塔缺少旧版电击抗性或气体/恐惧免疫");
		assertProperties(new SpiderQueen(), Char.Property.BEAST, Char.Property.BOSS);
		assertProperties(new SpiderQueen.SpiderEgg(), Char.Property.UNKNOW, Char.Property.BOSS);
		assertProperties(new SpiderQueen.SpiderWorker(), Char.Property.BEAST);
		assertProperties(new SpiderQueen.SpiderMind(), Char.Property.BEAST);
		assertProperties(new SpiderQueen.SpiderJumper(), Char.Property.BEAST);
		assertProperties(new SpiderQueen.SpiderGold(), Char.Property.BEAST);

		assertProperties(new LichDancer(), Char.Property.UNDEAD, Char.Property.MAGICER, Char.Property.BOSS);
		assertProperties(new LichDancer.BatteryTomb(), Char.Property.MECH, Char.Property.MINIBOSS);
		assertProperties(new LichDancer.LinkBomb(), Char.Property.MECH, Char.Property.MINIBOSS);
		assertProperties(new ElderAvatar(), Char.Property.ALIEN, Char.Property.BOSS);
		assertProperties(new ElderAvatar.TheHunter(), Char.Property.ALIEN, Char.Property.BOSS);
		assertProperties(new ElderAvatar.TheWarlock(), Char.Property.ALIEN, Char.Property.BOSS);
		assertProperties(new ElderAvatar.TheMonk(), Char.Property.ALIEN, Char.Property.BOSS);
		assertProperties(new ElderAvatar.TheMech(), Char.Property.ALIEN, Char.Property.MECH, Char.Property.BOSS);
		assertProperties(new ElderAvatar.Obelisk(), Char.Property.UNKNOW, Char.Property.BOSS);
		assertProperties(new King(), Char.Property.DWARF, Char.Property.BOSS);
		assertProperties(new King.Undead(), Char.Property.UNDEAD, Char.Property.BOSS);
		assertProperties(new King.DwarfKingTomb(), Char.Property.UNKNOW, Char.Property.BOSS);

		Yog yog = new Yog();
		assertProperties(yog, Char.Property.UNKNOW, Char.Property.BOSS);
		check(!Char.hasProp(yog, Char.Property.IMMOVABLE)
				&& yog.resist(pd.items.equipment.weapon.enchantments.EnchantmentDark.class) == 0.5f
				&& yog.resist(pd.items.consum.scrolls.ScrollOfPsionicBlast.class) < 1f
				&& yog.resist(pd.actors.buffs.Amok.class) == 0.5f
				&& !yog.isImmune(pd.actors.buffs.Amok.class),
				"Yog本体的旧版位移或暗属性/灵能/狂乱防御错误");
		Yog.RottingFist rotting = new Yog.RottingFist();
		Yog.BurningFist burning = new Yog.BurningFist();
		Yog.InfectingFist infecting = new Yog.InfectingFist();
		Yog.PinningFist pinning = new Yog.PinningFist();
		assertProperties(rotting, Char.Property.ELEMENT, Char.Property.BOSS);
		assertProperties(burning, Char.Property.ELEMENT, Char.Property.BOSS);
		assertProperties(infecting, Char.Property.ELEMENT, Char.Property.BOSS);
		assertProperties(pinning, Char.Property.ELEMENT, Char.Property.BOSS);
		check(rotting.resist(pd.actors.buffs.Poison.class) == 0.5f
				&& burning.resist(pd.actors.blobs.Fire.class) == 0.5f
				&& infecting.resist(pd.actors.buffs.Poison.class) == 0.5f
				&& pinning.resist(pd.actors.buffs.Burning.class) == 0.5f,
				"Yog四拳的旧版元素抗性不完整");
		assertProperties(new Yog.Larva(), Char.Property.UNKNOW);
		Dungeon.depth = 1;
	}

	private static void assertProperties(Char target, Char.Property... expected) {
		for (Char.Property property : expected) {
			check(Char.hasProp(target, property), target.getClass().getSimpleName()
					+ " 缺少旧版阵营属性 " + property);
		}
	}

	private static void testMissingMainBossRecovery() {
		Dungeon.branch = 0;
		for (int depth : new int[]{5, 10, 15, 20, 25}) {
			Dungeon.depth = depth;
			Actor.clear();
			Random.pushGenerator(0x5245434F56455200L + depth);
			try {
				Level source;
				Level restored;
				Class<? extends Mob> expected;
				if (depth == 5) {
					SpsSewerBossLevel level = new SpsSewerBossLevel();
					source = level;
					restored = new SpsSewerBossLevel();
					prepareHeadlessLevel(source);
					check(level.build(), "下水道首领恢复测试地图构建失败");
					int variant = level.bossVariantForTesting();
					expected = variant == SpsSewerBossLevel.GOO ? SpsGoo.class
							: variant == SpsSewerBossLevel.SEWER_HEART ? SewerHeart.class : PlagueDoctor.class;
				} else if (depth == 10) {
					SpsPrisonBossLevel level = new SpsPrisonBossLevel();
					source = level;
					restored = new SpsPrisonBossLevel();
					prepareHeadlessLevel(source);
					check(level.build(), "监狱首领恢复测试地图构建失败");
					int variant = level.bossVariantForTesting();
					expected = variant == SpsPrisonBossLevel.TENGU ? SpsTengu.class
							: variant == SpsPrisonBossLevel.PRISON_WANDER ? PrisonWander.class : Tank.class;
				} else if (depth == 15) {
					SpsCavesBossLevel level = new SpsCavesBossLevel();
					source = level;
					restored = new SpsCavesBossLevel();
					prepareHeadlessLevel(source);
					check(level.build(), "洞穴首领恢复测试地图构建失败");
					int variant = level.bossVariantForTesting();
					expected = variant == SpsCavesBossLevel.HYBRID ? Hybrid.class
							: variant == SpsCavesBossLevel.DM300 ? SpsDM300.class : SpiderQueen.class;
					Level.set(level.arenaDoorForTesting(), Terrain.WALL, level);
				} else if (depth == 20) {
					SpsCityBossLevel level = new SpsCityBossLevel();
					source = level;
					restored = new SpsCityBossLevel();
					prepareHeadlessLevel(source);
					check(level.build(), "矮人城首领恢复测试地图构建失败");
					int variant = level.bossVariantForTesting();
					expected = variant == SpsCityBossLevel.LICH_DANCER ? LichDancer.class
							: variant == SpsCityBossLevel.ELDER_AVATAR ? ElderAvatar.class : King.class;
					Level.set(SpsCityBossLevel.ARENA_DOOR, Terrain.WALL, level);
				} else {
					SpsHallsBossLevel level = new SpsHallsBossLevel();
					source = level;
					restored = new SpsHallsBossLevel();
					prepareHeadlessLevel(source);
					check(level.build(), "恶魔大厅首领恢复测试地图构建失败");
					expected = Yog.class;
				}

				source.locked = true;
				Level.set(source.entrance(), Terrain.WALL_DECO, source);
				Bundle bundle = new Bundle();
				source.storeInBundle(bundle);
				bundle.put("entered", true);
				Dungeon.level = restored;
				restored.restoreFromBundle(bundle);
				check(restored.locked, "首领缺失读档后楼层未保持封锁，深度=" + depth);
				check(countMobs(restored, expected) == 1,
						"首领缺失读档后没有补回已选变体，深度=" + depth + "，类型=" + expected.getSimpleName());
				checkLegacyVisual(restored,
						depth == 5 ? Assets.Environment.SPS_TILES_SEWERS_LEGACY
								: depth == 10 ? Assets.Environment.SPS_TILES_PRISON_LEGACY
								: depth == 15 ? Assets.Environment.SPS_TILES_CAVES_LEGACY
								: depth == 20 ? Assets.Environment.SPS_TILES_CITY_LEGACY
								: Assets.Environment.SPS_TILES_HALLS_LEGACY,
						depth == 5 ? Assets.Environment.SPS_WATER_SEWERS
								: depth == 10 ? Assets.Environment.SPS_WATER_PRISON
								: depth == 15 ? Assets.Environment.SPS_WATER_CAVES
								: depth == 20 ? Assets.Environment.SPS_WATER_CITY
								: Assets.Environment.SPS_WATER_HALLS,
						"首领缺失恢复", depth);
			} finally {
				Random.popGenerator();
			}
		}
		Actor.clear();
		Dungeon.level = null;
		Dungeon.depth = 1;
	}

	private static void testOrphanSewerMinionRecovery() {
		Dungeon.depth = 5;
		Dungeon.branch = 0;
		HashSet<Integer> recovered = new HashSet<>();
		for (int seed = 0; seed < 256 && recovered.size() < 2; seed++) {
			Actor.clear();
			Random.pushGenerator(0x53455745524F0000L + seed);
			try {
				SpsSewerBossLevel source = new SpsSewerBossLevel();
				prepareHeadlessLevel(source);
				check(source.build(), "下水道孤立随从恢复测试地图构建失败");
				int variant = source.bossVariantForTesting();
				if (variant == SpsSewerBossLevel.GOO || recovered.contains(variant)) continue;

				Mob orphan;
				Class<? extends Mob> expected;
				if (variant == SpsSewerBossLevel.SEWER_HEART) {
					orphan = new SewerHeart.SewerLasher();
					expected = SewerHeart.class;
				} else {
					orphan = new PlagueDoctor.ShadowRat();
					expected = PlagueDoctor.class;
				}
				orphan.pos = SpsFixedBossLevel.BOSS_CELL + 1;
				source.mobs().add(orphan);
				source.locked = true;
				Level.set(source.entrance(), Terrain.WALL_DECO, source);

				Bundle bundle = new Bundle();
				source.storeInBundle(bundle);
				bundle.put("entered", true);
				SpsSewerBossLevel restored = new SpsSewerBossLevel();
				Dungeon.level = restored;
				restored.restoreFromBundle(bundle);
				check(countMobs(restored, expected) == 1,
						"孤立首领随从阻止主首领补回，变体=" + variant);
				recovered.add(variant);
			} finally {
				Random.popGenerator();
			}
		}
		check(recovered.size() == 2, "确定种子没有覆盖下水道之心和瘟疫医生的孤立随从恢复");
		Actor.clear();
		Dungeon.level = null;
		Dungeon.depth = 1;
	}

	private static void prepareHeadlessLevel(Level level) {
		level.transitions = new ArrayList<>();
		level.customTiles = new ArrayList<>();
		level.customTerrain = new ArrayList<>();
		level.customWalls = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		level.blobs = new java.util.HashMap<>();
		level.plants = new SparseArray<>();
		level.traps = new SparseArray<>();
	}

	private static void testZotPrison() {
		Hero previousHero = Dungeon.hero;
		Dungeon.depth = 14;
		Dungeon.branch = Palantir.BRANCH;
		Dungeon.zotKilled = false;
		for (int seed = 0; seed < 16; seed++) {
			Random.pushGenerator(0x5A4F540000L + seed);
			try {
				pd.actors.Actor.clear();
				ZotBossLevel level = new ZotBossLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.mobs().clear();
				level.heaps = new SparseArray<>();
				level.blobs = new java.util.HashMap<>();
				check(level.build(), "Zot监牢构建失败，种子=" + seed);
				level.buildFlagMaps();
				Dungeon.level = level;
				check(level.width() == 48 && level.height() == 48, "Zot监牢必须为48x48");
				check(level.map[level.entrance()] == Terrain.PEDESTAL, "Zot监牢中央基座错误");
				check(level.customTiles.size() == 1
						&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
						"Zot监牢原始魔法洞窟图层缺失");
				boolean[] access = reachable(level, level.entrance(), true);
				int reachableCells = 0;
				boolean reachedOutside = false;
				for (int cell = 0; cell < access.length; cell++) {
					if (!access[cell]) continue;
					reachableCells++;
					int x = cell % level.width();
					int y = cell / level.width();
					if (x < 21 || x > 27 || y < 21 || y > 27) reachedOutside = true;
				}
				check(reachedOutside && reachableCells > 100,
						"Zot监牢中央监牢无法进入外部战区，种子=" + seed);
				int spawn = level.safeBossCell(level.entrance());
				check(level.passable[spawn], "Zot没有可用生成点，种子=" + seed);
			} finally {
				Random.popGenerator();
			}
		}
		Zot zot = new Zot();
		check(zot.HT == 25000 && zot.defenseSkill == 40 && zot.EXP == 20,
				"Zot旧版基础数值错误");
		check(zot.attackSkill(null) == 90, "Zot旧版命中错误");
		ZotPhase phase = new ZotPhase();
		check(phase.HT == 200 && phase.defenseSkill == 40 && phase.EXP == 30
				&& phase.attackSkill(null) == 149 && phase.attackDelay() == 2f,
				"Zot虚像旧版基础数值错误");
		check(phase.resist(pd.actors.blobs.Electricity.class) == 0.5f,
				"Zot虚像缺少旧版电击抗性");
		check(zot.isImmune(pd.items.equipment.weapon.enchantments.EnchantmentDark.class)
				&& zot.isImmune(pd.items.consum.scrolls.ScrollOfPsionicBlast.class),
				"Zot缺少旧版暗属性或灵能震爆免疫");

		MagicEye eyeStats = new MagicEye();
		check(eyeStats.HT >= 496 && eyeStats.HT <= 793 && eyeStats.defenseSkill == 89
				&& eyeStats.attackSkill(null) == 129 && eyeStats.damageRoll() == 1
				&& eyeStats.attackDelay() == 2f && eyeStats.SupercreateLoot() instanceof StoneOre,
				"Zot魔眼没有使用旧版深度成长、攻击或施法速度");

		Bundle legacyPalantir = new Bundle();
		legacyPalantir.put("depth", 18);
		legacyPalantir.put("pos", 321);
		Palantir migratedPalantir = new Palantir();
		migratedPalantir.restoreFromBundle(legacyPalantir);
		Bundle migratedPalantirState = new Bundle();
		migratedPalantir.storeInBundle(migratedPalantirState);
		check(migratedPalantirState.getInt("return_depth") == 18
				&& migratedPalantirState.getInt("return_pos") == 321
				&& migratedPalantirState.getInt("depth") == 18
				&& migratedPalantirState.getInt("pos") == 321,
				"帕兰提尔没有迁移并双写旧版depth/pos返回点");
		Bundle preferredPalantir = new Bundle();
		preferredPalantir.put("return_depth", 17);
		preferredPalantir.put("return_pos", 123);
		preferredPalantir.put("depth", 18);
		preferredPalantir.put("pos", 321);
		migratedPalantir.restoreFromBundle(preferredPalantir);
		migratedPalantirState = new Bundle();
		migratedPalantir.storeInBundle(migratedPalantirState);
		check(migratedPalantirState.getInt("return_depth") == 17
				&& migratedPalantirState.getInt("return_pos") == 123,
				"帕兰提尔没有优先读取当前返回点字段");

		Random.pushGenerator(0x5A4F544445415448L);
		try {
			Actor.clear();
			Hero hero = new Hero();
			hero.HP = hero.HT = 1000;
			hero.lvl = 100;
			Dungeon.hero = hero;
			ZotBossLevel live = new ZotBossLevel();
			prepareHeadlessLevel(live);
			check(live.build(), "Zot真实击杀地图构建失败");
			live.buildFlagMaps();
			Dungeon.level = live;
			hero.pos = 24 + 24 * live.width();
			Actor.add(hero);
			AutoPotion autoPotion = new AutoPotion();
			autoPotion.activate(hero);
			TestZot healingZot = new TestZot();
			healingZot.pos = 22 + 24 * live.width();
			healingZot.state = healingZot.PASSIVE;
			healingZot.HP = healingZot.HT - 10;
			Actor.add(healingZot);
			int beforeHealing = healingZot.HP;
			healingZot.runLegacyAct();
			check(healingZot.HP - beforeHealing == 1,
					"装备自动药剂时Zot每回合必须只回复1点生命");
			healingZot.HP = healingZot.HT - 10;
			healingZot.paralysed = 1;
			beforeHealing = healingZot.HP;
			healingZot.runLegacyAct();
			check(healingZot.HP - beforeHealing == 6,
					"Zot麻痹回合必须额外回复5点并保留自动药剂的1点回复");
			Actor.remove(healingZot);

			TestMagicEye beamEye = new TestMagicEye();
			beamEye.pos = 22 + 24 * live.width();
			Actor.add(beamEye);
			int beforeBeam = hero.HP;
			check(beamEye.aim(hero), "Zot魔眼没有建立旧版直线光束");
			beamEye.deathGaze();
			check(beforeBeam - hero.HP >= 20 && beforeBeam - hero.HP <= 50,
					"Zot魔眼光束伤害没有保持旧版20至50区间：" + (beforeBeam - hero.HP));
			Actor.remove(beamEye);

			int trigger = -1;
			for (int cell = 0; cell < live.length(); cell++) {
				int x = cell % live.width();
				int y = cell / live.width();
				if (live.passable[cell] && (x < 21 || x > 27 || y < 21 || y > 27)) {
					trigger = cell;
					break;
				}
			}
			check(trigger >= 0, "Zot真实击杀测试没有找到外部战区");
			hero.pos = trigger;
			live.map[trigger] = Terrain.EMPTY;
			live.pressCell(trigger);
			Zot spawned = null;
			for (Mob mob : live.mobs()) if (mob instanceof Zot) spawned = (Zot) mob;
			check(spawned != null && spawned.pos != hero.pos, "踏出中央监牢没有生成Zot");

			live.mobs().remove(spawned);
			Bundle interrupted = new Bundle();
			live.storeInBundle(interrupted);
			ZotBossLevel recovered = new ZotBossLevel();
			recovered.restoreFromBundle(interrupted);
			Zot recoveredZot = null;
			for (Mob mob : recovered.mobs()) if (mob instanceof Zot) recoveredZot = (Zot) mob;
			check(recoveredZot != null, "Zot战中坏档没有补回缺失首领");

			Dungeon.level = recovered;
			Actor.clear();
			Actor.add(hero);
			Actor.add(recoveredZot);
			ZotPhase cleanupPhase = new ZotPhase();
			cleanupPhase.pos = recovered.safeBossCell(recoveredZot.pos + 1);
			cleanupPhase.sprite = new SilentCharSprite();
			recovered.mobs().add(cleanupPhase);
			Actor.add(cleanupPhase);
			MagicEye cleanupEye = new MagicEye();
			cleanupEye.pos = recovered.safeBossCell(recoveredZot.pos - 1);
			cleanupEye.sprite = new SilentCharSprite();
			recovered.mobs().add(cleanupEye);
			Actor.add(cleanupEye);
			int deathCell = recoveredZot.pos;
			recoveredZot.sprite = new SilentCharSprite();
			recoveredZot.HP = 0;
			recoveredZot.die(hero);
			Heap soul = recovered.heaps.get(deathCell);
			check(Dungeon.zotKilled && !recovered.locked
					&& soul != null && soul.items.stream().anyMatch(item -> item instanceof SoulCollect),
					"Zot真实死亡没有解锁监牢或掉落灵魂收集石");
			for (Mob mob : recovered.mobs()) {
				check(!(mob instanceof ZotPhase) && !(mob instanceof MagicEye),
						"Zot死亡后仍残留虚像或魔眼");
			}
			Bundle completed = new Bundle();
			recovered.storeInBundle(completed);
			ZotBossLevel completedReload = new ZotBossLevel();
			completedReload.restoreFromBundle(completed);
			check(!completedReload.locked, "Zot完成态存档恢复后重新锁死");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = previousHero;
			Dungeon.level = null;
			Dungeon.branch = 0;
			Dungeon.zotKilled = false;
		}
	}

	private static void testGnollKingQuest() {
		Hero previousHero = Dungeon.hero;
		Dungeon.depth = 4;
		Dungeon.branch = AdventureJournal.branchFor(14);
		Dungeon.gnollKingKilled = false;
		Random.pushGenerator(0x474E4F4C4C4B494EL);
		try {
			pd.actors.Actor.clear();
			Hero hero = new Hero();
			hero.HP = hero.HT = 100;
			hero.lvl = Hero.MAX_LEVEL + 3;
			AdventureJournal journal = new AdventureJournal();
			hero.belongings.backpack.items.add(journal);
			Dungeon.hero = hero;
			FieldBossLevel level = new FieldBossLevel();
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			level.customTerrain = new ArrayList<>();
			level.customWalls = new ArrayList<>();
			level.mobs().clear();
			level.heaps = new SparseArray<>();
			level.blobs = new java.util.HashMap<>();
			level.plants = new SparseArray<>();
			level.traps = new SparseArray<>();
			check(level.build(), "豺狼王竞技场构建失败");
			level.buildFlagMaps();
			Dungeon.level = level;
			check(level.width() == 48 && level.height() == 48, "豺狼王竞技场必须为48x48");
			check(level.entrance() / level.width() >= 19 && level.entrance() / level.width() <= 20,
					"豺狼王竞技场出生基座纵坐标错误");
			check(level.map[17 * level.width() + 23] == Terrain.DOOR, "豺狼王竞技场中央门坐标错误");
			check(level.customTiles.size() == 1 && level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
					"豺狼王竞技场原始森林图层缺失");
			boolean[] access = reachable(level, level.entrance(), true);
			check(access[16 * level.width() + 23], "豺狼王竞技场出生点无法进入主战区");
			GnollKing king = new GnollKing();
			check(king.HT == 1000 && king.defenseSkill == 0 && king.EXP == 30,
					"豺狼王旧版基础数值错误");
			check(new GnollArcher().attackSkill(null) == 30 && new GnollArcher().EXP == 1,
					"豺狼弓箭手旧版基础数值错误");
			check(new UYog().createLoot() instanceof PowerHand, "始祖之眼必须掉落力量之手");
			check(king.isImmune(pd.items.equipment.weapon.enchantments.EnchantmentDark.class),
					"豺狼王缺少旧版暗属性附魔免疫");
			check(king.SupercreateLoot() instanceof GnollMark,
					"豺狼王特殊奖励必须为旧版仪式面具");

			TestGnollKing phased = new TestGnollKing();
			phased.HP = 749;
			check(phased.runLegacyAct() && phased.phase() == 1 && phased.cooldown() == 0f
					&& phased.speed() == 3f,
					"豺狼王75%阶段切换必须立即进入四倍速逃跑且不额外耗时");
			phased.HP = 499;
			check(phased.runLegacyAct() && phased.phase() == 2 && phased.cooldown() == 0f,
					"豺狼王50%阶段切换错误或额外耗时");
			phased.HP = 249;
			check(phased.runLegacyAct() && phased.phase() == 3 && phased.cooldown() == 0f,
					"豺狼王25%阶段切换错误或额外耗时");

			hero.pos = 16 * level.width() + FieldBossLevel.CENTER;
			level.map[hero.pos] = Terrain.EMPTY;
			Actor.add(hero);
			level.pressCell(hero.pos);
			GnollKing spawned = null;
			for (Mob mob : level.mobs()) if (mob instanceof GnollKing) spawned = (GnollKing) mob;
			check(spawned != null && spawned.pos / level.width() < 17 && spawned.pos != hero.pos,
					"踏入原野主战区没有在旧版战区随机生成豺狼王");

			level.mobs().remove(spawned);
			Bundle interrupted = new Bundle();
			level.storeInBundle(interrupted);
			FieldBossLevel recovered = new FieldBossLevel();
			recovered.restoreFromBundle(interrupted);
			GnollKing recoveredKing = null;
			for (Mob mob : recovered.mobs()) if (mob instanceof GnollKing) recoveredKing = (GnollKing) mob;
			check(recoveredKing != null, "原野首领战坏档没有补回缺失的豺狼王");

			Dungeon.level = recovered;
			Actor.clear();
			Actor.add(hero);
			Actor.add(recoveredKing);
			int deathCell = recoveredKing.pos;
			recoveredKing.sprite = new SilentCharSprite();
			recoveredKing.HP = 0;
			recoveredKing.die(hero);
			check(journal.isCompleted(14) && Dungeon.gnollKingKilled && !recovered.locked,
					"豺狼王真实死亡没有同步旧击杀状态、完成异界日志目的地14或解除封锁");
			Heap loot = recovered.heaps.get(deathCell);
			int gold = 0;
			boolean ring = false;
			boolean clothes = false;
			if (loot != null) for (Item item : loot.items) {
				if (item instanceof Gold) gold += item.quantity();
				else if (item instanceof AdamantRing) ring = true;
				else if (item instanceof GnollClothes) clothes = true;
			}
			check(ring && clothes && gold >= 1000 && gold <= 1499,
					"豺狼王旧版死亡掉落错误：金币=" + gold + "，戒指=" + ring + "，衣物=" + clothes);
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = previousHero;
			Dungeon.level = null;
			Dungeon.branch = 0;
			Dungeon.gnollKingKilled = false;
		}
	}

	private static void testBossRushLevel() {
		Hero previousHero = Dungeon.hero;
		Dungeon.depth = 14;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(21);
		Random.pushGenerator(0x424F535352555348L);
		try {
			pd.actors.Actor.clear();
			BossRushLevel level = new BossRushLevel();
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			level.customTerrain = new ArrayList<>();
			level.customWalls = new ArrayList<>();
			level.mobs().clear();
			level.heaps = new SparseArray<>();
			level.blobs = new java.util.HashMap<>();
			level.plants = new SparseArray<>();
			level.traps = new SparseArray<>();
			check(level.build(), "BossRushLevel构建失败");
			level.buildFlagMaps();
			Dungeon.level = level;
			check(level.width() == 24 && level.height() == 24, "BossRush竞技场必须为24x24");
			check(level.map[BossRushLevel.ENTRANCE] == Terrain.PEDESTAL, "BossRush中心出生基座错误");
			check(level.entrance() == BossRushLevel.ENTRANCE, "BossRush入口过渡缺失");
			check(level.map[level.arenaExit()] == Terrain.LOCKED_EXIT, "BossRush顶部出口必须初始锁定");
			check(level.transitions.size() == 2, "BossRush必须同时具有入口和完成出口过渡");
			check(level.customTiles.size() == 1 && level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
					"BossRush原始魔法洞窟图层缺失");
			boolean[] access = reachable(level, BossRushLevel.ENTRANCE, false);
			check(access[level.arenaExit() + level.width()], "BossRush出生点无法到达顶部出口通道");
			for (int lane = 0; lane < 5; lane++) {
				boolean found = false;
				for (int x = 2 + lane * 4; x < 6 + lane * 4 && !found; x++) {
					for (int y = 2; y <= 22; y++) {
						int terrain = level.map[x + y * level.width()];
						if (terrain != Terrain.WALL && terrain != Terrain.WALL_DECO) { found = true; break; }
					}
				}
				check(found, "BossRush第" + (lane + 1) + "条纵向通道缺失");
			}
			level.createMobs();
			check(level.mobs().size() == 1 && level.mobs().iterator().next() instanceof Dragonking,
					"BossRush首战必须为异界龙王");
			Dragonking dragon = (Dragonking) level.mobs().iterator().next();
			check(dragon.HT == 100 && dragon.defenseSkill == 0 && dragon.EXP == 1,
					"异界龙王旧版基础数值错误");
			GoldenSkeletonKey dragonKey = new TestDragonking().legacyKey();
			check(dragonKey.depth == Dungeon.depth, "异界龙王没有掉落当前深度的旧版骷髅万能钥匙");
			Class<?>[] expected = {Dragonking.class, UGoo.class, UTengu.class, UDM300.class,
					UKing.class, UIcecorps.class, UIcecorps2.class, UYog.class, UAmulet.class};
			check(java.util.Arrays.equals(BossRushLevel.BOSS_SEQUENCE, expected),
					"BossRush九阶段顺序错误");
			check(new UGoo().HT == 1000 && new UTengu().HT == 1000 && new UDM300().HT == 1000,
					"BossRush前三个终极首领生命值错误");
			check(new UKing().HT == 2000 && new UIcecorps().HT == 1500
					&& new UIcecorps2().HT == 1500 && new UYog().HT == 1000 && new UAmulet().HT == 1000,
					"BossRush后续首领生命值错误");

			level.mobs().clear();
			UDM300 savedBoss = new UDM300();
			savedBoss.pos = BossRushLevel.ENTRANCE;
			level.mobs().add(savedBoss);
			Bundle legacy = new Bundle();
			level.storeInBundle(legacy);
			legacy.remove("boss_stage");
			legacy.remove("arena_exit");
			legacy.remove("completed");
			legacy.put("stairs", -1);
			BossRushLevel migrated = new BossRushLevel();
			migrated.restoreFromBundle(legacy);
			check(migrated.arenaExit() == level.arenaExit()
					&& migrated.map[migrated.arenaExit()] == Terrain.LOCKED_EXIT,
					"BossRush旧存档没有从地图恢复真实出口");
			check(migrated.bossStage() == 3 && !migrated.completed(),
					"BossRush旧存档没有从存活首领恢复阶段");
			Bundle migratedState = new Bundle();
			migrated.storeInBundle(migratedState);
			check(migratedState.getInt("arena_exit") == migratedState.getInt("stairs"),
					"BossRush没有双写旧版出口字段");

			Actor.clear();
			Hero hero = new Hero();
			hero.HP = hero.HT = 100;
			hero.lvl = Hero.MAX_LEVEL + 3;
			AdventureJournal journal = new AdventureJournal();
			hero.belongings.backpack.items.add(journal);
			Dungeon.hero = hero;
			BossRushLevel live = new BossRushLevel();
			live.transitions = new ArrayList<>();
			live.customTiles = new ArrayList<>();
			live.customTerrain = new ArrayList<>();
			live.customWalls = new ArrayList<>();
			live.mobs().clear();
			live.heaps = new SparseArray<>();
			live.blobs = new java.util.HashMap<>();
			live.plants = new SparseArray<>();
			live.traps = new SparseArray<>();
			check(live.build(), "BossRush真实死亡链地图构建失败");
			live.buildFlagMaps();
			Dungeon.level = live;
			hero.pos = BossRushLevel.ENTRANCE;
			Actor.add(hero);
			live.createMobs();
			for (int stage = 0; stage < expected.length; stage++) {
				check(live.mobs().size() == 1,
						"BossRush第" + stage + "阶段首领数量错误：" + live.mobs().size());
				Mob boss = live.mobs().iterator().next();
				check(boss.getClass() == expected[stage],
						"BossRush真实死亡链第" + stage + "阶段类型错误：" + boss.getClass());
				boss.sprite = new SilentCharSprite();
				Actor.add(boss);
				boss.die(hero);
				check(live.bossStage() == stage + 1,
						"BossRush真实死亡后阶段未推进，阶段=" + stage);
			}
			check(live.mobs().isEmpty(), "BossRush最终首领死亡后仍残留阶段首领");
			check(live.completed() && !live.locked
					&& live.map[live.arenaExit()] == Terrain.UNLOCKED_EXIT,
					"BossRush九战结束后未解锁真实出口");
			check(journal.isCompleted(21), "BossRush九战结束后未完成异界日志目的地21");
			Bundle completedRush = new Bundle();
			live.storeInBundle(completedRush);
			BossRushLevel completedReload = new BossRushLevel();
			completedReload.restoreFromBundle(completedRush);
			check(completedReload.bossStage() == expected.length && completedReload.completed()
					&& !completedReload.locked
					&& completedReload.map[completedReload.arenaExit()] == Terrain.UNLOCKED_EXIT,
					"BossRush九战完成态存档往返不一致");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = previousHero;
			Dungeon.level = null;
			Dungeon.branch = 0;
		}
	}

	private static final class TestDragonking extends Dragonking {
		GoldenSkeletonKey legacyKey() {
			return createLegacyKey();
		}
	}

	private static final class TestGnollKing extends GnollKing {
		boolean runLegacyAct() {
			return super.act();
		}

		int phase() {
			Bundle state = new Bundle();
			storeInBundle(state);
			return state.getInt("breaks");
		}
	}

	private static final class TestMagicEye extends MagicEye {
		boolean aim(Char target) {
			return super.canAttack(target);
		}

		@Override
		public int attackSkill(Char target) {
			return 1000;
		}
	}

	private static final class TestZot extends Zot {
		boolean runLegacyAct() {
			return super.act();
		}
	}

	private static void testFestivalDrops() {
		HeartOfScarecrow target = new HeartOfScarecrow();
		check(target.capacity() == 35, "草靶子容量必须为35");
		check(target.canHold(new Dagger()) && target.canHold(new ClothArmor()),
				"草靶子必须收纳近战武器和护甲");
		check(!target.canHold(new Gold()), "草靶子不应收纳金币");
		check(new AdultDragonViolet().createLoot() instanceof BossRush,
				"城镇守卫巨龙必须掉落BossRush挑战");
		check(new YearPetEgg().image == pd.atlas.items.SpecificPlaceHolderDict.SOMETHING_0,
				"年兽之魂原版图标索引错误");
		YearPet pet = new YearPet();
		check(pet.HT == 500 && pet.legacyType() == 666,
				"年兽宝宝基础生命或旧版类型编号错误");
		check(WndAflyInfo.createResult(new pd.items.Item[]{
				new Strawberry(), new SmallRation(), new ChargrilledMeat()}) instanceof AflyFood,
				"阿飞饭团配方错误");
		check(WndAflyInfo.createResult(new pd.items.Item[]{
				new Strawberry(), new HealGrass(), new HealGrass()})
				instanceof pd.items.equipment.armor.fusion.LifeArmor,
				"阿飞生命护甲配方错误");
		check(WndAflyInfo.createResult(new pd.items.Item[]{
				new AflyFood(), new Ankh(), null}) instanceof AflyEgg,
				"阿飞哨子配方错误");
		check(WndAflyInfo.createResult(new pd.items.Item[]{
				new Gold(), null, null}) instanceof Garbage,
				"阿飞错误配方必须产生垃圾");
	}

	private static void testSafeLevels() {
		Hero previousHero = Dungeon.hero;
		Dungeon.hero = null;
		int[][] layouts = {
				SaveRoomLayouts.ROOM_OF_GRASS,
				SaveRoomLayouts.ROOM_OF_FOREST,
				SaveRoomLayouts.SAFE_ROOM_DEFAULT
		};
		for (int roomType = 0; roomType < layouts.length; roomType++) {
			Statistics.roomType = roomType;
			Dungeon.branch = 20;
			Random.pushGenerator(0x53414645L + roomType);
			try {
				SafeLevel level = new SafeLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				level.heaps = new SparseArray<>();
				check(level.build(), "SafeLevel构建失败，房型=" + roomType);
				check(level.firstVisitForTesting(), "SafeLevel无日志首次进入状态错误");
				check(level.width() == 48 && level.height() == 48, "SafeLevel必须为48x48");
				check(level.map[SafeLevel.ENTRANCE] == Terrain.ENTRANCE, "SafeLevel入口坐标错误");
				check(level.entrance() == SafeLevel.ENTRANCE, "SafeLevel入口过渡缺失");
				check(level.customTiles.size() == 1
						&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
						"SafeLevel原始整图图层缺失");
				for (int cell = 0; cell < layouts[roomType].length; cell++) {
					if (cell != SafeLevel.ENTRANCE) {
						check(level.map[cell] == layouts[roomType][cell],
								"SafeLevel布局不一致，房型=" + roomType + "，格=" + cell);
					}
				}

				level.createItems();
				check(!level.heaps.valueList().isEmpty(), "SafeLevel固定种子未生成任何补给");
				for (Heap heap : level.heaps.valueList()) {
					check(layouts[roomType][heap.pos] == Terrain.EMPTY, "SafeLevel补给生成在非空地形");
					if (heap.peek() instanceof Gold) {
						check(heap.type == Heap.Type.CHEST, "SafeLevel金币容器类型错误");
						check(heap.peek().quantity() >= 25 && heap.peek().quantity() < 50,
								"SafeLevel金币数量错误");
					} else if (heap.peek() instanceof SmallMeat) {
						check(heap.type == Heap.Type.M_WEB, "SafeLevel肉干容器类型错误");
					} else {
						check(heap.peek() instanceof YellowDewdrop && heap.type == Heap.Type.E_DUST,
								"SafeLevel露珠容器类型错误");
					}
				}
			} finally {
				Random.popGenerator();
			}
		}

		Hero completedHero = new Hero();
		AdventureJournal completedJournal = new AdventureJournal();
		Bundle completedState = new Bundle();
		completedState.put("completed", 1);
		completedJournal.restoreFromBundle(completedState);
		completedHero.belongings.backpack.items.add(completedJournal);
		Dungeon.hero = completedHero;
		int revisitGold = 0;
		for (int seed = 0; seed < 8; seed++) {
			Random.pushGenerator(0x534146455245L + seed);
			try {
				Statistics.roomType = seed % layouts.length;
				SafeLevel revisit = new SafeLevel();
				revisit.transitions = new ArrayList<>();
				revisit.customTiles = new ArrayList<>();
				revisit.heaps = new SparseArray<>();
				check(revisit.build(), "SafeLevel重访构建失败，种子=" + seed);
				check(!revisit.firstVisitForTesting(), "SafeLevel完成后仍按首次进入处理");
				revisit.createItems();
				for (Heap heap : revisit.heaps.valueList()) {
					if (heap.peek() instanceof Gold) {
						revisitGold++;
						check(heap.peek().quantity() >= 1 && heap.peek().quantity() < 10,
								"SafeLevel重访金币未恢复为1至9");
					}
				}
			} finally {
				Random.popGenerator();
			}
		}
		check(revisitGold > 0, "SafeLevel重访固定种子未覆盖金币奖励");
		Dungeon.hero = previousHero;
	}

	private static void testSokobanIntroLevel() {
		Dungeon.depth = 1;
		Dungeon.branch = 21;
		Random.pushGenerator(0x534F4B4FL);
		try {
			SokobanIntroLevel level = new SokobanIntroLevel();
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			level.customTerrain = new ArrayList<>();
			level.customWalls = new ArrayList<>();
			level.mobs().clear();
			level.heaps = new SparseArray<>();
			level.blobs = new java.util.HashMap<>();
			level.plants = new SparseArray<>();
			level.traps = new SparseArray<>();
			check(level.build(), "SokobanIntroLevel构建失败");
			check(level.width() == 48 && level.height() == 48, "推箱第一关必须为48x48");
			check(SokobanIntroLevel.ENTRANCE == 151, "推箱第一关入口常量错误");
			check(level.map[SokobanIntroLevel.ENTRANCE] == Terrain.ENTRANCE, "推箱第一关入口坐标错误");
			check(level.entrance() == SokobanIntroLevel.ENTRANCE, "推箱第一关入口过渡缺失");
			check(level.customTiles.size() == 1
					&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
					"推箱第一关原始整图图层缺失");
			for (int cell = 0; cell < SokobanLayouts.SOKOBAN_INTRO_LEVEL.length; cell++) {
				if (cell != SokobanIntroLevel.ENTRANCE) {
					check(level.map[cell] == SokobanLayouts.SOKOBAN_INTRO_LEVEL[cell],
							"推箱第一关布局不一致，格=" + cell);
				}
			}

			checkTerrainCount(level, Terrain.SOKOBAN_SHEEP, 5, "普通绵羊");
			checkTerrainCount(level, Terrain.CORNER_SOKOBAN_SHEEP, 2, "斜推绵羊");
			checkTerrainCount(level, Terrain.SWITCH_SOKOBAN_SHEEP, 8, "换位绵羊");
			checkTerrainCount(level, Terrain.BLACK_SOKOBAN_SHEEP, 4, "黑色绵羊");
			checkTerrainCount(level, Terrain.FLEECING_TRAP, 15, "破坏陷阱");
			checkTerrainCount(level, Terrain.CHANGE_SHEEP_TRAP, 10, "转换陷阱");
			checkTerrainCount(level, Terrain.SOKOBAN_ITEM_REVEAL, 2, "奖励开关");
			checkTerrainCount(level, Terrain.SOKOBAN_HEAP, 5, "奖励箱");
			checkTerrainCount(level, Terrain.PORT_WELL, 1, "传送门");
			checkTerrainCount(level, Terrain.SOKOBAN_PORT_SWITCH, 1, "传送开关");
			check(level.map[SokobanIntroLevel.PORTAL] == Terrain.PORT_WELL, "推箱第一关传送门坐标错误");
			check(level.map[SokobanIntroLevel.PORTAL_SWITCH] == Terrain.SOKOBAN_PORT_SWITCH,
					"推箱第一关传送开关坐标错误");
			check((Terrain.flags[level.map[SokobanIntroLevel.PORTAL]] & Terrain.PASSABLE) != 0,
					"推箱第一关传送门不可站立");
			check((Terrain.flags[level.map[SokobanIntroLevel.PORTAL_SWITCH]] & Terrain.PASSABLE) != 0,
					"推箱第一关传送开关不可站立");
			check((Terrain.flags[level.map[SokobanIntroLevel.PORTAL_DESTINATION]] & Terrain.PASSABLE) != 0,
					"推箱第一关传送目标不可站立");
			boolean[] beforeKey = reachable(level, SokobanIntroLevel.ENTRANCE, false);
			check(beforeKey[1126], "推箱第一关首个奖励开关从入口不可达");
			check(beforeKey[SokobanIntroLevel.KEY_PRIZE_CELL], "推箱第一关铁钥匙掉落点从入口不可达");
			boolean[] afterKey = reachable(level, SokobanIntroLevel.ENTRANCE, true);
			check(afterKey[SokobanIntroLevel.PORTAL_SWITCH], "推箱第一关传送开关在开锁后仍不可达");
			check(afterKey[SokobanIntroLevel.PORTAL], "推箱第一关传送门在开锁后仍不可达");
			check(afterKey[1303], "推箱第一关第二个奖励开关在开锁后仍不可达");
			boolean[] afterPortal = reachable(level, SokobanIntroLevel.PORTAL_DESTINATION, true);
			check(afterPortal[SokobanIntroLevel.TOWEL_PRIZE_CELL], "推箱第一关毛巾掉落点经传送门不可达");

			level.createMobs();
			check(level.mobs().size() == 19, "推箱第一关绵羊总数错误");
			checkMobCount(level, SheepSokoban.class, 5);
			checkMobCount(level, SheepSokobanCorner.class, 2);
			checkMobCount(level, SheepSokobanSwitch.class, 8);
			checkMobCount(level, SheepSokobanBlack.class, 4);

			Bundle current = new Bundle();
			level.storeInBundle(current);
			check(current.getInt("prize_no") == current.getInt("prizeNo")
					&& current.getIntArray("destinationspots")[0] == 0,
					"推箱第一关未双写旧版状态字段");
			check(Arrays.equals(current.getIntArray("heapgenspots"), new int[]{
					SokobanIntroLevel.KEY_PRIZE_CELL, SokobanIntroLevel.TOWEL_PRIZE_CELL}),
					"推箱第一关旧版奖励坐标写出错误");

			current.remove("portal_destination");
			current.remove("prize_no");
			current.put("destinationspots", new int[]{SokobanIntroLevel.PORTAL_DESTINATION, 0, 0});
			current.put("prizeNo", 1);
			SokobanIntroLevel migrated = new SokobanIntroLevel();
			migrated.restoreFromBundle(current);
			Bundle migratedState = new Bundle();
			migrated.storeInBundle(migratedState);
			check(migratedState.getInt("portal_destination") == SokobanIntroLevel.PORTAL_DESTINATION
					&& migratedState.getInt("prize_no") == 1,
					"推箱第一关未读取旧版传送或奖励进度");

			Bundle precedence = new Bundle();
			level.storeInBundle(precedence);
			precedence.put("portal_destination", SokobanIntroLevel.PORTAL_DESTINATION);
			precedence.put("prize_no", 2);
			precedence.put("destinationspots", new int[]{0});
			precedence.put("prizeNo", 0);
			SokobanIntroLevel preferred = new SokobanIntroLevel();
			preferred.restoreFromBundle(precedence);
			Bundle preferredState = new Bundle();
			preferred.storeInBundle(preferredState);
			check(preferredState.getInt("portal_destination") == SokobanIntroLevel.PORTAL_DESTINATION
					&& preferredState.getInt("prize_no") == 2,
					"推箱第一关新存档字段未优先于旧字段");
		} finally {
			Random.popGenerator();
		}
	}

	private static void checkTerrainCount(Level level, int terrain, int expected, String label) {
		int actual = 0;
		for (int value : level.map) if (value == terrain) actual++;
		check(actual == expected, label + "数量错误，预期=" + expected + "，实际=" + actual);
	}

	private static void testSokobanCastle() {
		Hero previousHero = Dungeon.hero;
		Dungeon.depth = 1;
		Dungeon.branch = 22;
		Dungeon.hero = null;
		SokobanCastle level = new SokobanCastle();
		level.transitions = new ArrayList<>();
		level.customTiles = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		check(level.build(), "SokobanCastle构建失败");
		check(level.width() == 48 && level.height() == 48, "推箱城堡必须为48x48");
		check(SokobanCastle.ENTRANCE == 1080, "推箱城堡入口常量错误");
		check(level.map[SokobanCastle.ENTRANCE] == Terrain.ENTRANCE, "推箱城堡入口坐标错误");
		check(level.entrance() == SokobanCastle.ENTRANCE, "推箱城堡入口过渡缺失");
		check(level.customTiles.size() == 1
				&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
				"推箱城堡原始整图图层缺失");
		for (int cell = 0; cell < SokobanLayouts.SOKOBAN_CASTLE.length; cell++) {
			if (cell != SokobanCastle.ENTRANCE) {
				check(level.map[cell] == SokobanLayouts.SOKOBAN_CASTLE[cell],
						"推箱城堡布局不一致，格=" + cell);
			}
		}
		checkTerrainCount(level, Terrain.SOKOBAN_SHEEP, 2, "城堡普通绵羊");
		checkTerrainCount(level, Terrain.CORNER_SOKOBAN_SHEEP, 36, "城堡斜推绵羊");
		checkTerrainCount(level, Terrain.SWITCH_SOKOBAN_SHEEP, 20, "城堡换位绵羊");
		checkTerrainCount(level, Terrain.BLACK_SOKOBAN_SHEEP, 5, "城堡黑色绵羊");
		checkTerrainCount(level, Terrain.FLEECING_TRAP, 104, "城堡破坏陷阱");
		checkTerrainCount(level, Terrain.CHANGE_SHEEP_TRAP, 32, "城堡转换陷阱");
		checkTerrainCount(level, Terrain.SOKOBAN_ITEM_REVEAL, 12, "城堡奖励开关");
		checkTerrainCount(level, Terrain.SOKOBAN_HEAP, 25, "城堡奖励箱");
		checkTerrainCount(level, Terrain.PORT_WELL, 3, "城堡传送门");
		checkTerrainCount(level, Terrain.SOKOBAN_PORT_SWITCH, 1, "城堡传送开关");
		for (int portal : SokobanCastle.PORTALS) {
			check(level.map[portal] == Terrain.PORT_WELL, "推箱城堡传送门坐标错误，格=" + portal);
		}
		check(level.map[SokobanCastle.PORTAL_SWITCH] == Terrain.SOKOBAN_PORT_SWITCH,
				"推箱城堡传送开关坐标错误");
		check((Terrain.flags[level.map[SokobanCastle.SENTINEL_CELL]] & Terrain.PASSABLE) != 0,
				"推箱城堡守卫坐标不可站立");
		for (int i = 0; i < SokobanCastle.PRIZE_CELLS.length; i++) {
			int cell = SokobanCastle.PRIZE_CELLS[i];
			if (i == 8 || i == 9) {
				check(level.map[cell] == Terrain.SHRUB, "推箱城堡隐藏奖励必须位于可燃灌木，格=" + cell);
			} else {
				check((Terrain.flags[level.map[cell]] & Terrain.PASSABLE) != 0,
						"推箱城堡奖励坐标不可站立，格=" + cell);
			}
		}
		Random.pushGenerator(0x534F4B4F43415354L);
		try {
			level.createItems();
			for (Heap heap : level.heaps.valueList()) {
				Item item = heap.peek();
				if (item instanceof Gold) {
					check(item.quantity() >= 400 && item.quantity() < 800,
							"推箱城堡首次金币未保持400至799");
				}
			}
		} finally {
			Random.popGenerator();
		}

		Hero completedHero = new Hero();
		AdventureJournal completedJournal = new AdventureJournal();
		Bundle completedState = new Bundle();
		completedState.put("completed", 1 << 2);
		completedJournal.restoreFromBundle(completedState);
		completedHero.belongings.backpack.items.add(completedJournal);
		Dungeon.hero = completedHero;
		SokobanCastle revisit = new SokobanCastle();
		revisit.transitions = new ArrayList<>();
		revisit.customTiles = new ArrayList<>();
		revisit.mobs().clear();
		revisit.heaps = new SparseArray<>();
		check(revisit.build(), "推箱城堡重访构建失败");
		check(!revisit.hasBonusPrizes(), "推箱城堡完成后仍保留首次限定奖励池");
		Random.pushGenerator(0x534F4B4F52455649L);
		try {
			revisit.createItems();
			int goldChests = 0;
			for (Heap heap : revisit.heaps.valueList()) {
				Item item = heap.peek();
				check(!(item instanceof ScrollOfUpgrade), "推箱城堡重访仍生成升级卷轴");
				if (item instanceof Gold) {
					goldChests++;
					check(item.quantity() >= 300 && item.quantity() < 500,
							"推箱城堡重访金币未恢复为300至499");
				}
			}
			check(goldChests > 0, "推箱城堡重访未生成金币宝箱");
		} finally {
			Random.popGenerator();
		}
		Dungeon.hero = previousHero;
	}

	private static void testSokobanTeleportLevel() {
		Dungeon.depth = 1;
		Dungeon.branch = 23;
		SokobanTeleportLevel level = new SokobanTeleportLevel();
		level.transitions = new ArrayList<>();
		level.customTiles = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		check(level.build(), "SokobanTeleportLevel构建失败");
		check(level.width() == 48 && level.height() == 48, "推箱传送关必须为48x48");
		check(SokobanTeleportLevel.ENTRANCE == 776, "推箱传送关入口常量错误");
		check(level.map[SokobanTeleportLevel.ENTRANCE] == Terrain.ENTRANCE,
				"推箱传送关入口坐标错误");
		check(level.entrance() == SokobanTeleportLevel.ENTRANCE, "推箱传送关入口过渡缺失");
		for (int cell = 0; cell < SokobanLayouts.SOKOBAN_TELEPORT_LEVEL.length; cell++) {
			if (cell != SokobanTeleportLevel.ENTRANCE) {
				check(level.map[cell] == SokobanLayouts.SOKOBAN_TELEPORT_LEVEL[cell],
						"推箱传送关布局不一致，格=" + cell);
			}
		}
		checkTerrainCount(level, Terrain.SOKOBAN_SHEEP, 3, "传送关普通绵羊");
		checkTerrainCount(level, Terrain.CORNER_SOKOBAN_SHEEP, 24, "传送关斜推绵羊");
		checkTerrainCount(level, Terrain.SWITCH_SOKOBAN_SHEEP, 8, "传送关换位绵羊");
		checkTerrainCount(level, Terrain.BLACK_SOKOBAN_SHEEP, 2, "传送关黑色绵羊");
		checkTerrainCount(level, Terrain.FLEECING_TRAP, 82, "传送关破坏陷阱");
		checkTerrainCount(level, Terrain.CHANGE_SHEEP_TRAP, 21, "传送关转换陷阱");
		checkTerrainCount(level, Terrain.SOKOBAN_ITEM_REVEAL, 12, "传送关奖励开关");
		checkTerrainCount(level, Terrain.SOKOBAN_HEAP, 21, "传送关奖励箱");
		checkTerrainCount(level, Terrain.PORT_WELL, 22, "传送关传送门");
		checkTerrainCount(level, Terrain.SOKOBAN_PORT_SWITCH, 10, "传送关传送开关");
		for (int portal : SokobanTeleportLevel.PORTALS) {
			check(level.map[portal] == Terrain.PORT_WELL, "推箱传送门坐标错误，格=" + portal);
		}
		for (int cell : SokobanTeleportLevel.SWITCHES) {
			check(level.map[cell] == Terrain.SOKOBAN_PORT_SWITCH, "推箱传送开关坐标错误，格=" + cell);
		}
		for (int cell : SokobanTeleportLevel.SENTINELS) {
			check((Terrain.flags[level.map[cell]] & Terrain.PASSABLE) != 0,
					"推箱传送关守卫坐标不可站立，格=" + cell);
		}
	}

	private static void testSokobanPuzzlesLevel() {
		Dungeon.depth = 1;
		Dungeon.branch = 24;
		SokobanPuzzlesLevel level = new SokobanPuzzlesLevel();
		level.transitions = new ArrayList<>();
		level.customTiles = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		check(level.build(), "SokobanPuzzlesLevel构建失败");
		check(level.width() == 48 && level.height() == 48, "推箱谜题集必须为48x48");
		check(SokobanPuzzlesLevel.ENTRANCE == 543, "推箱谜题集入口常量错误");
		check(level.map[SokobanPuzzlesLevel.ENTRANCE] == Terrain.ENTRANCE, "推箱谜题集入口坐标错误");
		check(level.entrance() == SokobanPuzzlesLevel.ENTRANCE, "推箱谜题集入口过渡缺失");
		for (int cell = 0; cell < SokobanLayouts.SOKOBAN_PUZZLE_LEVEL.length; cell++) {
			if (cell != SokobanPuzzlesLevel.ENTRANCE) {
				check(level.map[cell] == SokobanLayouts.SOKOBAN_PUZZLE_LEVEL[cell],
						"推箱谜题集布局不一致，格=" + cell);
			}
		}
		checkTerrainCount(level, Terrain.SOKOBAN_SHEEP, 6, "谜题集普通绵羊");
		checkTerrainCount(level, Terrain.CORNER_SOKOBAN_SHEEP, 4, "谜题集斜推绵羊");
		checkTerrainCount(level, Terrain.SWITCH_SOKOBAN_SHEEP, 38, "谜题集换位绵羊");
		checkTerrainCount(level, Terrain.BLACK_SOKOBAN_SHEEP, 10, "谜题集黑色绵羊");
		checkTerrainCount(level, Terrain.FLEECING_TRAP, 60, "谜题集破坏陷阱");
		checkTerrainCount(level, Terrain.CHANGE_SHEEP_TRAP, 56, "谜题集转换陷阱");
		checkTerrainCount(level, Terrain.SOKOBAN_ITEM_REVEAL, 15, "谜题集奖励开关");
		checkTerrainCount(level, Terrain.SOKOBAN_HEAP, 22, "谜题集奖励箱");
		checkTerrainCount(level, Terrain.PORT_WELL, 7, "谜题集传送门");
		checkTerrainCount(level, Terrain.SOKOBAN_PORT_SWITCH, 4, "谜题集传送开关");
		for (int portal : SokobanPuzzlesLevel.PORTALS) {
			check(level.map[portal] == Terrain.PORT_WELL, "推箱谜题集传送门坐标错误，格=" + portal);
		}
		for (int cell : SokobanPuzzlesLevel.SWITCHES) {
			check(level.map[cell] == Terrain.SOKOBAN_PORT_SWITCH, "推箱谜题集传送开关坐标错误，格=" + cell);
		}
		for (int portal : SokobanPuzzlesLevel.SWITCH_PORTALS) {
			check(level.map[portal] == Terrain.PORT_WELL, "推箱谜题集开关指向非传送门，格=" + portal);
		}
		check(SokobanPuzzlesLevel.SWITCH_PORTALS[2] == SokobanPuzzlesLevel.PORTALS[1],
				"推箱谜题集第三开关必须指向地图中的(32,15)传送门");
		check(level.nonKeyPrize(0) instanceof pd.items.KnowledgeBook,
				"推箱谜题集常驻知识之书奖励缺失");
		check(level.nonKeyPrize(2) instanceof pd.items.consum.eggs.Egg,
				"推箱谜题集首次宠物蛋奖励缺失");
		for (int cell : SokobanPuzzlesLevel.SENTINELS) {
			check((Terrain.flags[level.map[cell]] & Terrain.PASSABLE) != 0,
					"推箱谜题集守卫坐标不可站立，格=" + cell);
		}
	}

	private static void testSokobanRuntimeAndPersistence() {
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Hero previousHero = Dungeon.hero;
		Level previousLevel = Dungeon.level;
		int previousDepth = Dungeon.depth;
		int previousBranch = Dungeon.branch;
		try {
			Actor.clear();
			Notes.reset();
			Dungeon.depth = 1;
			Dungeon.branch = AdventureJournal.branchFor(3);
			Hero hero = new Hero();
			hero.HP = hero.HT = 100;
			Dungeon.hero = hero;

			SokobanTeleportLevel level = new SokobanTeleportLevel();
			Dungeon.level = level;
			buildSokobanRuntimeLevel(level);
			hero.pos = level.entrance();

			SpsSokobanSheep switchSheep = new SheepSokoban();
			switchSheep.pos = SokobanTeleportLevel.SWITCHES[0];
			level.afterSheepMoved(switchSheep);
			check(level.map[switchSheep.pos] == Terrain.EMPTY,
					"推箱传送开关触发后未变为空地");
			Bundle switched = new Bundle();
			level.storeInBundle(switched);
			int[] destinations = switched.getIntArray("portal_destinations");
			int portalIndex = indexOf(SokobanTeleportLevel.PORTALS,
					SokobanTeleportLevel.SWITCH_PORTALS[0]);
			check(portalIndex >= 0 && destinations[portalIndex]
					== SokobanTeleportLevel.SWITCH_DESTINATIONS[0],
					"推箱传送开关未实时改变目标");
			check(switched.getInt("prizeNo") == switched.getInt("prize_no")
					&& Arrays.equals(switched.getIntArray("destinationspots"), destinations),
					"推箱关未双写旧版状态字段");

			SokobanTeleportLevel restored = new SokobanTeleportLevel();
			restored.restoreFromBundle(switched);
			Bundle restoredState = new Bundle();
			restored.storeInBundle(restoredState);
			check(Arrays.equals(destinations,
					restoredState.getIntArray("portal_destinations")),
					"推箱传送目标读档后改变");

			Bundle legacyOnly = switched;
			legacyOnly.remove("portal_destinations");
			legacyOnly.remove("prize_no");
			legacyOnly.remove("bonus_prizes");
			legacyOnly.remove("remaining_prizes");
			SokobanTeleportLevel legacyRestored = new SokobanTeleportLevel();
			legacyRestored.restoreFromBundle(legacyOnly);
			Bundle migrated = new Bundle();
			legacyRestored.storeInBundle(migrated);
			check(Arrays.equals(destinations, migrated.getIntArray("portal_destinations")),
					"推箱关未读取旧版destinationspots");
			check(migrated.getInt("prize_no") == switched.getInt("prizeNo"),
					"推箱关未读取旧版prizeNo");
			check(migrated.getIntArray("remaining_prizes").length
					== level.nonKeyPrizeCount(), "推箱关旧版奖励池迁移不完整");

			Bundle precedence = new Bundle();
			level.storeInBundle(precedence);
			int[] legacyConflict = precedence.getIntArray("destinationspots");
			legacyConflict[portalIndex] = 123;
			precedence.put("destinationspots", legacyConflict);
			SokobanTeleportLevel preferred = new SokobanTeleportLevel();
			preferred.restoreFromBundle(precedence);
			Bundle preferredState = new Bundle();
			preferred.storeInBundle(preferredState);
			check(preferredState.getIntArray("portal_destinations")[portalIndex]
					== SokobanTeleportLevel.SWITCH_DESTINATIONS[0],
					"推箱关新存档字段未优先于旧字段");

			check(level.prizeFor(0) instanceof IronKey,
					"推箱奖励序列未以六把铁钥匙开始");

			int change = findTerrain(level, Terrain.CHANGE_SHEEP_TRAP);
			SpsSokobanSheep black = new SheepSokobanBlack();
			black.pos = change;
			level.afterSheepMoved(black);
			check(level.map[change] == Terrain.CHANGE_SHEEP_TRAP,
					"黑羊错误触发了旧版不接受它的变形陷阱");

			int ordinaryChange = findTerrain(level, Terrain.CHANGE_SHEEP_TRAP);
			SpsSokobanSheep ordinary = new SheepSokoban();
			ordinary.pos = ordinaryChange;
			level.afterSheepMoved(ordinary);
			check(level.map[ordinaryChange] == Terrain.INACTIVE_TRAP
					&& level.mobs().findMob(ordinaryChange) instanceof SheepSokobanCorner,
					"普通羊踩变形陷阱后未替换为斜推羊");

			for (int cell = 0; cell < level.length(); cell++) {
				if (level.map[cell] == Terrain.FLEECING_TRAP) level.map[cell] = Terrain.EMPTY;
			}
			int start = 10 + 10 * level.width();
			int near = start + 4;
			int boundary = start + 5;
			level.map[near] = Terrain.FLEECING_TRAP;
			level.map[boundary] = Terrain.FLEECING_TRAP;
			boolean selectedBoundary = false;
			Random.pushGenerator(0x53484545504cL);
			try {
				for (int i = 0; i < 64; i++) {
					if (level.randomFleecingCell(start, 5) == boundary) selectedBoundary = true;
				}
			} finally {
				Random.popGenerator();
			}
			check(selectedBoundary, "黑羊无法选中旧版允许的五格边界陷阱");
			level.map[near] = Terrain.EMPTY;
			check(level.randomFleecingCell(start, 5) == -1,
					"只有五格边界陷阱时黑羊不应开始随机传送");

			level.resetPuzzle(hero);
			Bundle resetState = new Bundle();
			level.storeInBundle(resetState);
			check(!resetState.getBoolean("bonus_prizes")
					&& Arrays.equals(resetState.getIntArray("remaining_prizes"), new int[]{0}),
					"推箱失败重置后仍可重复获得首次额外奖励");

			testBlockedSokobanPushSpendsTurn(level, hero);
		} finally {
			Actor.clear();
			Dungeon.hero = previousHero;
			Dungeon.level = previousLevel;
			Dungeon.depth = previousDepth;
			Dungeon.branch = previousBranch;
			app.exit();
		}
	}

	private static void testBlockedSokobanPushSpendsTurn(SokobanCastle level, Hero hero) {
		int width = level.width();
		int heroCell = -1;
		int sheepCell = -1;
		for (int cell = width + 1; cell < level.length() - width - 1; cell++) {
			if (level.passable[cell] && level.passable[cell + 1] && level.solid[cell + 2]) {
				heroCell = cell;
				sheepCell = cell + 1;
				break;
			}
		}
		check(heroCell >= 0, "推箱地图缺少可测试的受阻推动位置");
		hero.pos = heroCell;
		SheepSokoban sheep = new SheepSokoban();
		sheep.pos = sheepCell;
		float before = hero.cooldown();
		sheep.interact(hero);
		check(sheep.pos == sheepCell && hero.pos == heroCell,
				"受阻推动错误移动了英雄或绵羊");
		check(hero.cooldown() > before, "受阻推动没有按旧版消耗一回合");
	}

	private static void buildSokobanRuntimeLevel(SokobanCastle level) {
		level.transitions = new ArrayList<>();
		level.customTiles = new ArrayList<>();
		level.customTerrain = new ArrayList<>();
		level.customWalls = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		level.blobs = new java.util.HashMap<>();
		level.plants = new SparseArray<>();
		level.traps = new SparseArray<>();
		check(level.build(), "推箱动态测试地图构建失败");
		level.buildFlagMaps();
	}

	private static int findTerrain(Level level, int terrain) {
		for (int cell = 0; cell < level.length(); cell++) {
			if (level.map[cell] == terrain) return cell;
		}
		throw new AssertionError("地图缺少地形：" + terrain);
	}

	private static int indexOf(int[] values, int target) {
		for (int i = 0; i < values.length; i++) if (values[i] == target) return i;
		return -1;
	}

	private static void testTownLevel() {
		Dungeon.depth = 1;
		Dungeon.branch = 25;
		TownLevel level = new TownLevel();
		level.transitions = new ArrayList<>();
		level.customTiles = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		check(level.build(), "TownLevel构建失败");
		check(level.width() == 48 && level.height() == 48, "城镇必须为48x48");
		check(TownLayouts.TOWN_LAYOUT.length == 2304, "城镇原始布局必须包含2304格");
		check(TownLevel.ENTRANCE == 1033, "城镇入口常量错误");
		check(TownLevel.LEGACY_EXIT == 1925, "城镇旧出口坐标错误");
		check(level.map[TownLevel.ENTRANCE] == Terrain.ENTRANCE, "城镇入口坐标错误");
		check(level.entrance() == TownLevel.ENTRANCE, "城镇入口过渡缺失");
		check(level.customTiles.size() == 1
				&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
				"城镇原始整图图层缺失");
		for (int cell = 0; cell < TownLayouts.TOWN_LAYOUT.length; cell++) {
			if (cell != TownLevel.ENTRANCE) {
				check(level.map[cell] == TownLayouts.TOWN_LAYOUT[cell],
						"城镇布局不一致，格=" + cell);
			}
		}
		check(TownLayouts.TOWN_LAYOUT[TownLevel.LEGACY_EXIT] == Terrain.STATUE,
				"城镇旧出口应保持原版雕像状态");
		check(TownLayouts.TOWN_LAYOUT[TownLevel.ALTAR_CELL] == Terrain.PEDESTAL,
				"城镇魔法矿石祭坛必须保持基座地形");
		for (int cell : TownLevel.BASE_CHESTS) {
			check((Terrain.flags[level.map[cell]] & Terrain.PASSABLE) != 0,
					"城镇基础宝箱坐标不可站立，格=" + cell);
		}
		for (int cell : TownLevel.TOMB_CHESTS) {
			check((Terrain.flags[level.map[cell]] & Terrain.PASSABLE) != 0,
					"城镇墓地宝箱坐标不可站立，格=" + cell);
		}
		for (int cell : TownLevel.FISH_CHESTS) {
			check((Terrain.flags[level.map[cell]] & Terrain.PASSABLE) != 0,
					"城镇鱼塘宝箱坐标不可站立，格=" + cell);
		}
		int[][] shopGroups = {
				TownLevel.EQUIPMENT_STORE_CELLS, TownLevel.GENERAL_STORE_CELLS,
				TownLevel.BOMB_STORE_CELLS, TownLevel.FOOD_STORE_CELLS,
				TownLevel.SPECIAL_STORE_CELLS, TownLevel.EGG_STORE_CELLS,
				TownLevel.GNOLL_STORE_CELLS, TownLevel.SKILL_STORE_CELLS,
				TownLevel.PILL_STORE_CELLS, TownLevel.ARMOR_STORE_CELLS
		};
		HashSet<Integer> shopCells = new HashSet<>();
		for (int[] group : shopGroups) for (int cell : group) {
			check((Terrain.flags[level.map[cell]] & Terrain.PASSABLE) != 0,
					"城镇商店货位不可站立，格=" + cell);
			check(shopCells.add(cell), "城镇商店货位重复，格=" + cell);
		}
		check(shopCells.size() == 40, "城镇十组商店必须共有40个旧版货位");
		check((Terrain.flags[level.map[TownLevel.TOMB_CELL]] & Terrain.PASSABLE) != 0,
				"城镇墓碑坐标不可站立");
		level.createMobs();
		check(TownLevel.RESIDENTS.length == TownLevel.RESIDENT_CELLS.length,
				"城镇居民身份表与坐标表长度不一致");
		check(TownLevel.TOMB_RESIDENTS.length == TownLevel.TOMB_RESIDENT_CELLS.length,
				"墓地区居民身份表与坐标表长度不一致");
		check(TownLevel.UNCLE_RESIDENTS.length == TownLevel.UNCLE_RESIDENT_CELLS.length,
				"工会居民身份表与坐标表长度不一致");
		check(TownLevel.EGG_RESIDENTS.length == TownLevel.EGG_RESIDENT_CELLS.length,
				"宠物店居民身份表与坐标表长度不一致");
		check(TownLevel.MOS_RESIDENTS.length == TownLevel.MOS_RESIDENT_CELLS.length,
				"酒馆居民身份表与坐标表长度不一致");
		check(TownLevel.SAR_RESIDENTS.length == TownLevel.SAR_RESIDENT_CELLS.length,
				"秘宝店居民身份表与坐标表长度不一致");
		check(TownLevel.RAIN_RESIDENTS.length == TownLevel.RAIN_RESIDENT_CELLS.length,
				"训练区居民身份表与坐标表长度不一致");
		check(TownLevel.FISH_RESIDENTS.length == TownLevel.FISH_RESIDENT_CELLS.length,
				"鱼塘居民身份表与坐标表长度不一致");
		check(level.mobs().size() == TownLevel.RESIDENTS.length + TownLevel.SHOPKEEPER_CELLS.length + 2,
				"城镇固定居民或店主数量错误");
		check(level.mobs().findMob(5 + 48 * 43) instanceof AdultDragonViolet,
				"城镇固定守卫巨龙坐标错误");
		AdultDragonViolet dragon = (AdultDragonViolet) level.mobs().findMob(5 + 48 * 43);
		check(dragon.HT == 8000 && dragon.defenseSkill == 40,
				"城镇守卫巨龙旧版生命或闪避数值错误");
		check(level.mobs().findMob(21 + 48 * 44) instanceof TestMob2,
				"城镇固定发条稻草人坐标错误");
		check(level.mobs().findMob(21 + 48 * 44).HT == 100000,
				"发条稻草人旧版生命值错误");
		for (int cell : TownLevel.SHOPKEEPER_CELLS) {
			check(level.mobs().findMob(cell) instanceof pd.actors.mobs.npcs.Shopkeeper,
					"城镇固定店主坐标错误，格=" + cell);
		}
		HashSet<Integer> occupiedCells = new HashSet<>();
		for (int i = 0; i < TownLevel.RESIDENTS.length; i++) {
			TownNpc found = null;
			for (Mob mob : level.mobs()) {
				if (mob.pos == TownLevel.RESIDENT_CELLS[i] && mob instanceof TownNpc) {
					found = (TownNpc) mob;
					break;
				}
			}
			check(found != null, "城镇居民坐标缺失，格=" + TownLevel.RESIDENT_CELLS[i]);
			check(found.spec() == TownLevel.RESIDENTS[i],
					"城镇居民身份与坐标不一致，格=" + TownLevel.RESIDENT_CELLS[i]);
			check(occupiedCells.add(found.pos), "城镇居民坐标重叠，格=" + found.pos);
		}
		check(level.map[23 + 48 * 12] == Terrain.WALL_GROUND,
				"LaJi所在墙格必须保持旧版地图原值");
		Mob ren = level.mobs().findMob(28 + 48 * 27);
		check(ren instanceof TownNpc && ((TownNpc) ren).spec() == TownNpc.Spec.RENNPC,
				"REN必须位于旧版固定坐标(28,27)");
	}

	private static void testNornAltar() {
		NornStone[] stones = {
				new GreenNornStone(), new BlueNornStone(), new OrangeNornStone(),
				new PurpleNornStone(), new YellowNornStone()
		};
		Class<?>[] products = {
				LokisFlail.class, NeptunusTrident.class, CromCruachAxe.class,
				AresSword.class, JupitersWraith.class
		};
		for (int i = 0; i < stones.length; i++) {
			TownLevel level = new TownLevel();
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			level.heaps = new SparseArray<>();
			check(level.build(), "祭坛测试城镇构建失败");
			Dungeon.level = level;
			Heap heap = new Heap();
			heap.pos = TownLevel.ALTAR_CELL;
			heap.drop(stones[i].quantity(2));
			level.heaps.put(heap.pos, heap);
			Weapon result = heap.consecrate();
			check(products[i].isInstance(result), "魔法矿石颜色与祝圣武器不匹配，类型=" + (i + 1));
			check(result.level() == 6 && result.isIdentified() && !result.cursed,
					"祭坛产物必须为已鉴定、未诅咒的+6武器");
		}
		Heap single = new Heap();
		single.drop(new GreenNornStone());
		check(single.consecrate() == null && single.size() == 1,
				"单块魔法矿石不应被祭坛消耗");
		Dungeon.level = null;
	}

	private static void testNewRoomLevel() {
		Dungeon.depth = 8;
		Dungeon.branch = 28;
		boolean[] generatedTypes = new boolean[2];
		for (int seed = 0; seed < 64 && !(generatedTypes[0] && generatedTypes[1]); seed++) {
			Random.pushGenerator(0x4E455752L + seed);
			try {
				NewRoomLevel level = new NewRoomLevel();
				level.transitions = new ArrayList<>();
				level.customTiles = new ArrayList<>();
				check(level.build(), "NewRoomLevel构建失败");
				check(level.width() == 48 && level.height() == 48, "新居必须为48x48");
				check(Statistics.roomType == 0 || Statistics.roomType == 1, "新居房型超出原版范围");
				generatedTypes[Statistics.roomType] = true;
				int[] expected = Statistics.roomType == 0
						? SaveRoomLayouts.SAFE_ROOM_DEFAULT : SaveRoomLayouts.ROOM_OF_FOREST;
				check(level.map[NewRoomLevel.ENTRANCE] == Terrain.ENTRANCE, "新居入口坐标错误");
				check(level.entrance() == NewRoomLevel.ENTRANCE, "新居入口过渡缺失");
				check(level.customTiles.size() == 1
						&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
						"新居原始整图图层缺失");
				for (int cell = 0; cell < expected.length; cell++) {
					if (cell != NewRoomLevel.ENTRANCE) {
						check(level.map[cell] == expected[cell],
								"新居布局不一致，房型=" + Statistics.roomType + "，格=" + cell);
					}
				}
			} finally {
				Random.popGenerator();
			}
		}
		check(generatedTypes[0] && generatedTypes[1], "新居固定种子未覆盖两种原版布局");
	}

	private static void testSpringFestivalLevel() {
		Hero previousHero = Dungeon.hero;
		Level previousLevel = Dungeon.level;
		Dungeon.depth = 6;
		Dungeon.branch = 26;
		Dungeon.level = null;
		SpringFestivalLevel level = new SpringFestivalLevel();
		level.transitions = new ArrayList<>();
		level.customTiles = new ArrayList<>();
		level.customTerrain = new ArrayList<>();
		level.customWalls = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		level.blobs = new java.util.HashMap<>();
		level.plants = new SparseArray<>();
		level.traps = new SparseArray<>();
		check(level.build(), "SpringFestivalLevel构建失败");
		check(level.width() == 48 && level.height() == 48, "春节镇必须为48x48");
		check(SpringFestivalLayouts.SPRING_FESTIVAL_LAYOUT.length == 2304,
				"春节镇原始布局必须包含2304格");
		check(level.map[SpringFestivalLevel.ENTRANCE] == Terrain.ENTRANCE, "春节镇入口坐标错误");
		check(level.entrance() == SpringFestivalLevel.ENTRANCE, "春节镇入口过渡缺失");
		check(level.customTiles.size() == 1
				&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
				"春节镇原始整图图层缺失");
		for (int cell = 0; cell < SpringFestivalLayouts.SPRING_FESTIVAL_LAYOUT.length; cell++) {
			if (cell != SpringFestivalLevel.ENTRANCE) {
				check(level.map[cell] == SpringFestivalLayouts.SPRING_FESTIVAL_LAYOUT[cell],
						"春节镇布局不一致，格=" + cell);
			}
		}
		for (int cell : new int[]{SpringFestivalLevel.FESTIVAL_JEWELRY_STORE,
				SpringFestivalLevel.FESTIVAL_SUPPLY_STORE}) {
			check((Terrain.flags[level.map[cell]] & Terrain.PASSABLE) != 0,
					"春节镇商店货位不可站立，格=" + cell);
		}
		Random.pushGenerator(0x535052494E47L);
		try {
			level.createItems();
			check(level.heaps.get(SpringFestivalLevel.FESTIVAL_JEWELRY_STORE) == null
					&& level.heaps.get(SpringFestivalLevel.FESTIVAL_SUPPLY_STORE) == null,
					"春节镇商店错误地在首次格子触发前生成");
			level.buildFlagMaps();
			level.storeStock();
		} finally {
			Random.popGenerator();
		}
		check(level.heaps.get(SpringFestivalLevel.FESTIVAL_JEWELRY_STORE) != null
				&& level.heaps.get(SpringFestivalLevel.FESTIVAL_JEWELRY_STORE).type == Heap.Type.FOR_SALE,
				"春节镇神器戒指商店未恢复");
		check(level.heaps.get(SpringFestivalLevel.FESTIVAL_SUPPLY_STORE) != null
				&& level.heaps.get(SpringFestivalLevel.FESTIVAL_SUPPLY_STORE).type == Heap.Type.FOR_SALE,
				"春节镇综合商店未恢复");

		level.heaps.remove(SpringFestivalLevel.FESTIVAL_SUPPLY_STORE);
		Bundle shopSave = new Bundle();
		level.storeInBundle(shopSave);
		check(Arrays.equals(shopSave.getIntArray("storespots"),
				new int[]{SpringFestivalLevel.FESTIVAL_JEWELRY_STORE})
				&& Arrays.equals(shopSave.getIntArray("bombpots"),
				new int[]{SpringFestivalLevel.FESTIVAL_SUPPLY_STORE}),
				"春节镇未保留旧版商店坐标字段");
		SpringFestivalLevel restoredShop = new SpringFestivalLevel();
		restoredShop.restoreFromBundle(shopSave);
		check(restoredShop.heaps.get(SpringFestivalLevel.FESTIVAL_JEWELRY_STORE) != null
				&& restoredShop.heaps.get(SpringFestivalLevel.FESTIVAL_SUPPLY_STORE) == null,
				"春节镇读档前错误改变商店库存");
		Random.pushGenerator(0x524553544F434BL);
		try {
			restoredShop.storeStock();
		} finally {
			Random.popGenerator();
		}
		check(restoredShop.heaps.get(SpringFestivalLevel.FESTIVAL_JEWELRY_STORE) != null
				&& restoredShop.heaps.get(SpringFestivalLevel.FESTIVAL_SUPPLY_STORE) != null,
				"春节镇读档后没有只补充已售空货位");
		level.createMobs();
		check(SpringFestivalLevel.RESTORED_RESIDENTS.length
				== SpringFestivalLevel.RESTORED_RESIDENT_CELLS.length,
				"春节镇居民身份表与坐标表长度不一致");
		check(level.mobs().size() == SpringFestivalLevel.RESTORED_RESIDENTS.length + 2,
				"春节镇已恢复居民数量错误");
		check(level.mobs().findMob(15 + 48 * 3) instanceof TestMob,
				"春节镇固定训练稻草人坐标错误");
		check(level.mobs().findMob(15 + 48 * 3).HT == 100000,
				"春节镇训练稻草人旧版生命值错误");
		check(level.mobs().findMob(6 + 48 * 44) instanceof YearBeast2,
				"春节镇固定年兽坐标错误");
		YearBeast2 yearBeast = (YearBeast2) level.mobs().findMob(6 + 48 * 44);
		check(yearBeast.HT == 1000 && yearBeast.defenseSkill == 30
				&& yearBeast.flying && yearBeast.viewDistance == 6,
				"春节镇年兽旧版基础数值错误");

		Actor.clear();
		Notes.reset();
		SpringFestivalLevel combatLevel = new InteractionSpringFestivalLevel();
		combatLevel.transitions = new ArrayList<>();
		combatLevel.customTiles = new ArrayList<>();
		combatLevel.mobs().clear();
		combatLevel.heaps = new SparseArray<>();
		combatLevel.blobs = new java.util.HashMap<>();
		combatLevel.traps = new SparseArray<>();
		check(combatLevel.build(), "春节镇年兽战斗测试地图构建失败");
		combatLevel.buildFlagMaps();
		Arrays.fill(combatLevel.heroFOV, true);
		Dungeon.level = combatLevel;
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.pos = combatLevel.entrance();
		AdventureJournal journal = new AdventureJournal();
		hero.belongings.backpack.items.add(journal);
		Dungeon.hero = hero;
		Actor.add(hero);
		YearBeast2 defeated = new YearBeast2();
		defeated.pos = 6 + SpringFestivalLevel.WIDTH * 44;
		defeated.sprite = new SilentCharSprite();
		combatLevel.mobs().add(defeated);
		Actor.add(defeated);
		defeated.die(hero);
		check(journal.isCompleted(6), "击败春节镇年兽后异界日志未完成目的地6");
		check(combatLevel.heaps.get(defeated.pos) != null
				&& combatLevel.heaps.get(defeated.pos).peek() instanceof YearPetEgg,
				"春节镇年兽死亡后未掉落年兽之魂");
		Actor.clear();
		Dungeon.hero = previousHero;
		Dungeon.level = previousLevel;
		Dungeon.branch = 0;
	}

	private static void testMinesBossLevel() {
		Dungeon.depth = 14;
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(7);
		check(Otiluke.completionDestination() == 7, "能源核心路线应完成目的地7");
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(19);
		check(Otiluke.completionDestination() == 19, "深层矿区路线应完成目的地19");
		Random.pushGenerator(0x4D494E45L);
		try {
			MinesBossLevel level = new MinesBossLevel();
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			level.mobs().clear();
			level.heaps = new SparseArray<>();
			check(level.build(), "MinesBossLevel构建失败");
			check(level.width() == 48 && level.height() == 48, "能源核心必须为48x48");
			check(MineBossLayouts.MINE_BOSS.length == 2304, "能源核心原始布局必须包含2304格");
			check(level.map[MinesBossLevel.ENTRANCE] == Terrain.ENTRANCE, "能源核心入口坐标错误");
			check(level.entrance() == MinesBossLevel.ENTRANCE, "能源核心入口过渡缺失");
			check(level.customTiles.size() == 1
					&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
					"能源核心原始整图图层缺失");
			for (int cell = 0; cell < MineBossLayouts.MINE_BOSS.length; cell++) {
				int original = MineBossLayouts.MINE_BOSS[cell];
				if (cell == MinesBossLevel.ENTRANCE) continue;
				if (original == Terrain.EMPTY) {
					check(level.map[cell] == Terrain.EMPTY || level.map[cell] == Terrain.WATER
							|| level.map[cell] == Terrain.OLD_HIGH_GRASS,
							"能源核心装饰改变了非允许地形，格=" + cell);
				} else {
					check(level.map[cell] == original, "能源核心固定地形不一致，格=" + cell);
				}
			}
			level.createMobs();
			int expectedSentinels = 0;
			int expectedTowers = 0;
			for (int terrain : MineBossLayouts.MINE_BOSS) {
				if (terrain == Terrain.SOKOBAN_SHEEP) expectedSentinels++;
				else if (terrain == Terrain.CORNER_SOKOBAN_SHEEP) expectedTowers++;
			}
			check(countMobs(level, MineSentinel.class) == expectedSentinels, "能源核心钢铁守卫数量错误");
			check(countMobs(level, LitTower.class) == expectedTowers, "能源核心雷电石像数量错误");
			check(countMobs(level, Otiluke.class) == 1, "能源核心腐化镜像数量错误");
			level.createItems();
			check(level.heaps.get(MinesBossLevel.KEY_CELL).type == Heap.Type.CHEST,
					"能源核心钥匙宝箱缺失");
			check(level.heaps.get(MinesBossLevel.PALANTIR_CELL).peek()
					instanceof pd.items.Palantir,
					"能源核心帕兰提尔缺失");

		} finally {
			Random.popGenerator();
		}
	}

	private static void testFleecingTrap() {
		Hero previousHero = Dungeon.hero;
		try {
			Hero hero = new Hero();
			Dungeon.hero = hero;
			ClothArmor armor = new ClothArmor();
			armor.cursed = true;
			hero.belongings.armor = armor;
			check(pd.levels.traps.FleecingTrap.destroyArmor(hero),
					"剥甲陷阱未摧毁已装备护甲");
			check(hero.belongings.armor == null, "剥甲陷阱触发后护甲槽未清空");
			check(!hero.belongings.backpack.contains(armor), "被摧毁的护甲错误返回背包");
			check(!pd.levels.traps.FleecingTrap.destroyArmor(hero),
					"无护甲时剥甲陷阱错误报告摧毁成功");
		} finally {
			Dungeon.hero = previousHero;
		}
	}

	private static SpsLegacyLevelVisual checkLegacyVisual(Level level, String tiles, String water,
			String label, int seed) {
		check(water.equals(level.waterTex()), label + "旧版水面资源错误，种子=" + seed);
		check(level.customTiles.size() == 1
				&& level.customTiles.get(0) instanceof SpsLegacyLevelVisual,
				label + "旧版整图视觉缺失，种子=" + seed);
		SpsLegacyLevelVisual visual = (SpsLegacyLevelVisual) level.customTiles.get(0);
		check(tiles.equals(visual.texturePath()), label + "旧版地砖资源错误，种子=" + seed);
		for (int cell = 0; cell < level.length(); cell++) {
			int expected = SpsLegacyLevelVisual.terrainVisual(level.map[cell]);
			check(expected >= 0 && expected < 64,
					label + "旧图索引越界，种子=" + seed + "，格=" + cell + "，索引=" + expected);
			check(visual.visualAt(cell) == expected,
					label + "旧图与地形不同步，种子=" + seed + "，格=" + cell);
		}
		return visual;
	}

	private static void checkLegacyCell(Level level, SpsLegacyLevelVisual visual, int cell,
			int expectedTerrain, String label) {
		check(level.map[cell] == expectedTerrain, label + "地形错误");
		check(visual.visualAt(cell) == SpsLegacyLevelVisual.terrainVisual(expectedTerrain),
				label + "视觉未同步");
	}

	private static void checkRestoredLegacyVisual(Level source, Level restored, String tiles,
			String water, String label) {
		if (source.plants == null) source.plants = new SparseArray<>();
		if (source.customTerrain == null) source.customTerrain = new ArrayList<>();
		if (source.customWalls == null) source.customWalls = new ArrayList<>();
		Bundle bundle = new Bundle();
		source.storeInBundle(bundle);
		restored.restoreFromBundle(bundle);
		checkLegacyVisual(restored, tiles, water, label + "读档", 0);
	}

	private static int countMobs(Level level, Class<? extends Mob> type) {
		int count = 0;
		for (Mob mob : level.mobs()) if (type.isInstance(mob)) count++;
		return count;
	}

	private static boolean[] reachable(Level level, int start, boolean unlockedDoors) {
		boolean[] result = new boolean[level.length()];
		ArrayDeque<Integer> queue = new ArrayDeque<>();
		result[start] = true;
		queue.add(start);
		while (!queue.isEmpty()) {
			int cell = queue.removeFirst();
			int x = cell % level.width();
			int y = cell / level.width();
			for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
				if (dx == 0 && dy == 0) continue;
				int nx = x + dx;
				int ny = y + dy;
				if (nx < 0 || nx >= level.width() || ny < 0 || ny >= level.height()) continue;
				int next = nx + ny * level.width();
				int terrain = level.map[next];
				if (terrain == Terrain.WALL || terrain == Terrain.STATUE
						|| (!unlockedDoors && terrain == Terrain.LOCKED_DOOR) || result[next]) continue;
				result[next] = true;
				queue.addLast(next);
			}
		}
		return result;
	}

	private static void checkMobCount(SokobanIntroLevel level, Class<?> type, int expected) {
		int actual = 0;
		for (Mob mob : level.mobs()) if (mob.getClass() == type) actual++;
		check(actual == expected, type.getSimpleName() + "数量错误，预期=" + expected + "，实际=" + actual);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class RecordingHeap extends Heap {
		private boolean opened;

		@Override
		public void open(Hero hero) {
			opened = true;
			type = Type.HEAP;
		}
	}

	private static final class SilentCharSprite extends CharSprite {
		@Override
		public void die() {
			// Headless tests have no animation timeline; the actor death path is still executed.
		}
	}

	private static final class InteractionCrabBossLevel extends CrabBossLevel {
		@Override
		public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
		}
	}

	private static final class InteractionSpringFestivalLevel extends SpringFestivalLevel {
		@Override
		public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
		}
	}

	private SpsFixedLevelTest() {
	}
}
