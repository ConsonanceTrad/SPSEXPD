package pd.levels;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.BoxStar;
import pd.actors.buffs.ExProtect;
import pd.actors.buffs.GlassShield;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.actors.mobs.BombBug;
import pd.actors.mobs.GoldThief;
import pd.actors.mobs.Golem;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Monk;
import pd.actors.mobs.MonsterBox;
import pd.actors.mobs.Rat;
import pd.actors.mobs.RedWraith;
import pd.actors.mobs.SpsExitMobs;
import pd.actors.mobs.Wraith;
import pd.actors.mobs.npcs.Blacksmith2;
import pd.actors.mobs.npcs.Blacksmith;
import pd.actors.mobs.npcs.Imp;
import pd.items.AdamantArmor;
import pd.items.AdamantRing;
import pd.items.AdamantWand;
import pd.items.AdamantWeapon;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StrBottle;
import pd.items.Stylus;
import pd.items.Triforce;
import pd.items.Weightstone;
import pd.items.armor.LeatherArmor;
import pd.items.food.Food;
import pd.items.keys.GoldenKey;
import pd.items.keys.SpsSkeletonKey;
import pd.items.potions.Potion;
import pd.items.quest.DwarfToken;
import pd.items.rings.Ring;
import pd.items.rings.RingOfAccuracy;
import pd.items.scrolls.Scroll;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.items.wands.WandOfMagicMissile;
import pd.items.weapon.melee.Mace;
import pd.levels.builders.SpsBspLayout;
import pd.levels.features.LevelTransition;
import pd.levels.traps.damagetrap.FireDamageTrap;
import pd.plants.Plant;
import pd.plants.SpsFruitBush;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Generates complete ordinary-floor maps without requiring a rendering context. */
public final class SpsRegularLevelTest {

	private static final int SEEDS_PER_REGION = 400;
	private static int pitRoomsValidated;

	public static void main(String[] args) {
		try {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(
				new ApplicationAdapter() { @Override public void create() { } },
				new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Dungeon.hero = new Hero();
		Scroll.initLabels();
		Potion.initColors();
		Ring.initGems();
		Blacksmith.Quest.reset();
		Class<?>[] types = {SewerLevel.class, PrisonLevel.class, CavesLevel.class,
				CityLevel.class, HallsLevel.class};
		int[] depths = {2, 7, 12, 17, 22};
		int generated = 0;
		for (int region = 0; region < types.length; region++) {
			int decoratedMaps = 0;
			for (int seed = 0; seed < SEEDS_PER_REGION; seed++) {
				Dungeon.depth = depths[region];
				Dungeon.branch = 0;
				Random.pushGenerator(0x5350534C4556454CL + region * 10_000L + seed);
				try {
					SpsRegularLevel level = newLevel(types[region]);
					level.feeling = feeling(seed);
					check(buildWithRetries(level), region, seed, "地图重试100次后仍构建失败");
					level.buildFlagMaps();
					validate(level, region, seed);
					if (region == 2 && seed == 0) validateLegacyBlacksmithRoom((CavesLevel)level);
					if (region == 2 && seed == 1) {
						check(countMobs(level, Blacksmith.class) == 0
								&& countMobs(level, Blacksmith2.class) == 0,
								region, seed, "旧版铁匠房在后续洞穴层重复生成");
					}
					if (countTerrain(level, Terrain.EMPTY_DECO)
							+ countTerrain(level, Terrain.WALL_DECO) > 0) decoratedMaps++;
					generated++;
				} finally {
					Random.popGenerator();
				}
			}
			check(decoratedMaps > 0, region, -3, "400个固定种子均未生成区域装饰");
		}
		check(pitRoomsValidated > 0, -1, -1, "2000张地图没有覆盖旧版陷坑房");
		validateHallsKeyDrop();
		validateSpecialRoomRotationPersistence();
		validateLegacyInitialMobCounts();
		validateLegacyRegionArmor();
		validateLegacyBranchDepthScaling();
		validateLegacyPrebuildSupplies();
		validateLegacyItemGeneration();
		validateLegacyRemains();
		validateExitGuardDepthTable();
		validateExitProtectionLifecycle();
		validateEnhancedPlantSuppression();
		validateLegacyBlacksmithRules();
		validateLegacyCityQuestSpawns();
		validateLegacyImpTokenDrops();
		System.out.println("SPS普通层完整地图测试通过：" + generated
				+ "张48x48地图，覆盖五区域装饰、怪物数量、区域护甲、旧版物资与出口守卫。" );
		app.exit();
		System.exit(0);
		} catch (Throwable t) {
			//SPS: 任何异常都必须让 headless JVM 退出，否则 gradle 会无限等待（曾导致门禁空转 12-38 分钟）
			t.printStackTrace();
			System.exit(1);
		}
	}

	private static SpsRegularLevel newLevel(Class<?> type) {
		if (type == SewerLevel.class) return new SewerLevel();
		if (type == PrisonLevel.class) return new PrisonLevel();
		if (type == CavesLevel.class) return new CavesLevel();
		if (type == CityLevel.class) return new CityLevel();
		return new HallsLevel();
	}

	private static void prepare(SpsRegularLevel level) {
		level.transitions = new ArrayList<>();
		level.mobs = new HashSet<>();
		level.heaps = new SparseArray<Heap>();
		level.blobs = new HashMap<>();
		level.plants = new SparseArray<Plant>();
		level.traps = new SparseArray<>();
		level.customTiles = new ArrayList<>();
		level.customTerrain = new ArrayList<>();
		level.customWalls = new ArrayList<>();
	}

	private static boolean buildWithRetries(SpsRegularLevel level) {
		for (int attempt = 0; attempt < 100; attempt++) {
			prepare(level);
			if (level.build()) return true;
		}
		return false;
	}

	private static Level.Feeling feeling(int seed) {
		switch (seed % 6) {
			case 0: return Level.Feeling.NONE;
			case 1: return Level.Feeling.CHASM;
			case 2: return Level.Feeling.WATER;
			case 3: return Level.Feeling.GRASS;
			case 4: return Level.Feeling.DARK;
			default: return Level.Feeling.SPECIAL_FLOOR;
		}
	}

	private static void validate(SpsRegularLevel level, int region, int seed) {
		check(level.width() == 48 && level.height() == 48, region, seed, "尺寸不是48x48");
		check(level.legacyRoomCount() >= 20, region, seed, "BSP房间少于20个");
		check(level.legacyConnectedRoomCount() >= 10, region, seed, "连通房间过少");
		check(level.legacyGenerationAttempts() > 0 && level.legacyGenerationAttempts() <= 64,
				region, seed, "构建次数越界");
		check(level.transitions.size() == 2, region, seed, "入口出口数量错误");
		check(level.map[level.entrance()] == Terrain.ENTRANCE, region, seed, "入口地形错误");
		int expectedExit = region == 4 ? Terrain.LOCKED_EXIT : Terrain.EXIT;
		check(level.map[level.exit()] == expectedExit, region, seed, "出口地形错误");
		check(reachable(level, level.entrance(), level.exit()), region, seed, "入口无法到达出口");
		for (LevelTransition transition : level.transitions) {
			check(transition.cell() >= 0 && transition.cell() < level.length(),
					region, seed, "楼层连接越界");
		}
		level.traps.valueList().forEach(trap ->
				check(trap.pos > level.width() && trap.pos < level.length() - level.width(),
						region, seed, "陷阱出现在地图边界"));
		validateExitGuard(level, region, seed);
		validateEnhancedEntrancePlant(level, region, seed);
		validatePitRoom(level, region, seed);
	}

	private static void validatePitRoom(SpsRegularLevel level, int region, int seed) {
		for (pd.levels.builders.SpsBspLayout.Room room
				: level.legacyLayout.rooms) {
			if (room.type != pd.levels.builders.SpsBspLayout.Type.PRISON_PIT) continue;
			pitRoomsValidated++;
			check(room.connected.size() == 1, region, seed, "陷坑房连接数量错误");
			pd.levels.builders.SpsBspLayout.Door door =
					room.connected.values().iterator().next();
			check(door != null && door.type
					== pd.levels.builders.SpsBspLayout.Door.Type.ONEWAY,
					region, seed, "陷坑房门没有保留旧版单向属性");
			check(level.map[door.x + door.y * level.width()] == Terrain.BROKEN_DOOR,
					region, seed, "陷坑房门没有绘制为破门");
			check(level.pitSign >= 0 && level.map[level.pitSign] == Terrain.SIGN,
					region, seed, "陷坑房专用提示牌缺失");
			int signX = level.pitSign % level.width();
			int signY = level.pitSign / level.width();
			check(signX > room.left && signX < room.right && signY > room.top && signY < room.bottom,
					region, seed, "陷坑房专用提示牌越界");
		}
	}

	private static void validateEnhancedEntrancePlant(SpsRegularLevel level, int region, int seed) {
		Plant plant = null;
		int enhanced = 0;
		for (Plant candidate : level.plants.valueList()) {
			int x = candidate.pos % level.width();
			int y = candidate.pos / level.width();
			if (candidate instanceof SpsFruitBush
					&& x > level.roomEntrance.left && x < level.roomEntrance.right
					&& y > level.roomEntrance.top && y < level.roomEntrance.bottom) {
				enhanced++;
				plant = candidate;
			}
		}
		check(enhanced == 1, region, seed, "主线入口房强化植物数量不是1，实际为" + enhanced);
		int x = plant.pos % level.width();
		int y = plant.pos / level.width();
		check(x > level.roomEntrance.left && x < level.roomEntrance.right
				&& y > level.roomEntrance.top && y < level.roomEntrance.bottom,
				region, seed, "强化植物不在入口房内部");
		check(plant.pos != level.entrance(), region, seed, "强化植物与入口楼梯重叠");
		check(level.map[plant.pos] != Terrain.SIGN && level.map[plant.pos] != Terrain.DEW_BLESS,
				region, seed, "强化植物与告示牌或露珠祝福重叠");
	}

	private static void validateExitGuard(SpsRegularLevel level, int region, int seed) {
		Mob guard = null;
		for (Mob mob : level.mobs) {
			if (!(mob instanceof SpsExitMobs.ExitGuard)) continue;
			int x = mob.pos % level.width();
			int y = mob.pos / level.width();
			if (x <= level.roomExit.left || x >= level.roomExit.right
					|| y <= level.roomExit.top || y >= level.roomExit.bottom) continue;
			check(guard == null, region, seed, "出口房生成了多个守卫");
			guard = mob;
		}
		check(guard != null, region, seed, "出口房没有生成旧版守卫");
		check(guard.pos % level.width() > level.roomExit.left
				&& guard.pos % level.width() < level.roomExit.right
				&& guard.pos / level.width() > level.roomExit.top
				&& guard.pos / level.width() < level.roomExit.bottom,
				region, seed, "出口守卫不在出口房内部");
		check(guard.spsOriginalGeneration && guard.buff(ExProtect.class) != null
				&& guard.paralysed == 1, region, seed, "出口守卫首次伤害保护错误");
		check(guard.buff(ShieldArmor.class) != null
				&& guard.buff(ShieldArmor.class).level() == Dungeon.depth * 5,
				region, seed, "出口守卫物理护甲错误");
		check(guard.buff(MagicArmor.class) != null
				&& guard.buff(MagicArmor.class).level() == Dungeon.depth * 5,
				region, seed, "出口守卫魔法护甲错误");
	}

	private static boolean reachable(Level level, int from, int to) {
		boolean[] seen = new boolean[level.length()];
		ArrayList<Integer> pending = new ArrayList<>();
		pending.add(from);
		while (!pending.isEmpty()) {
			int cell = pending.remove(pending.size() - 1);
			if (cell == to) return true;
			if (cell < 0 || cell >= seen.length || seen[cell] || !traversable(level.map[cell])) continue;
			seen[cell] = true;
			int x = cell % level.width();
			int y = cell / level.width();
			if (x > 0) pending.add(cell - 1);
			if (x < level.width() - 1) pending.add(cell + 1);
			if (y > 0) pending.add(cell - level.width());
			if (y < level.height() - 1) pending.add(cell + level.width());
		}
		return false;
	}

	private static boolean traversable(int terrain) {
		return (Terrain.flags[terrain] & Terrain.SOLID) == 0
				|| terrain == Terrain.DOOR || terrain == Terrain.SECRET_DOOR || terrain == Terrain.LOCKED_DOOR
				|| terrain == Terrain.BARRICADE || terrain == Terrain.BOOKSHELF
				|| terrain == Terrain.LOCKED_EXIT;
	}

	private static int countTerrain(Level level, int terrain) {
		int count = 0;
		for (int value : level.map) if (value == terrain) count++;
		return count;
	}

	private static void validateHallsKeyDrop() {
		Dungeon.depth = 22;
		Dungeon.branch = 0;
		Random.pushGenerator(0x53505348414C4C53L);
		try {
			HallsLevel level = new HallsLevel();
			check(buildWithRetries(level), 4, -1, "钥匙测试地图构建失败");
			level.addLegacyExitKeyToSpawn();
			int keys = 0;
			for (Item item : level.itemsToSpawn) {
				if (item instanceof SpsSkeletonKey && ((SpsSkeletonKey)item).depth == Dungeon.depth) keys++;
			}
			check(keys == 1, 4, -1, "大厅没有加入唯一的同层骷髅钥匙");
		} finally {
			Random.popGenerator();
		}
	}

	private static void validateSpecialRoomRotationPersistence() {
		Random.pushGenerator(0x535053524F4F4D53L);
		try {
			SpsRegularLevel.initLegacySpecialRooms();
			Bundle initialBundle = new Bundle();
			SpsRegularLevel.storeLegacySpecialRooms(initialBundle);
			String[] initial = initialBundle.getStringArray("sps_special_rooms");

			boolean rotated = false;
			for (int attempt = 0; attempt < 20 && !rotated; attempt++) {
				Dungeon.depth = 2 + attempt % 3;
				SewerLevel level = new SewerLevel();
				level.feeling = Level.Feeling.NONE;
				check(buildWithRetries(level), 0, -2, "特殊房轮换测试地图构建失败");
				Bundle currentBundle = new Bundle();
				SpsRegularLevel.storeLegacySpecialRooms(currentBundle);
				rotated = !Arrays.equals(initial, currentBundle.getStringArray("sps_special_rooms"));
			}
			check(rotated, 0, -2, "使用特殊房后牌组顺序没有轮换");

			SpsRegularLevel.restoreLegacySpecialRooms(initialBundle);
			Bundle restoredBundle = new Bundle();
			SpsRegularLevel.storeLegacySpecialRooms(restoredBundle);
			check(Arrays.equals(initial, restoredBundle.getStringArray("sps_special_rooms")),
					0, -2, "特殊房牌组存档恢复错误");
		} finally {
			Random.popGenerator();
		}
	}

	private static void validateLegacyInitialMobCounts() {
		boolean previousAmulet = Statistics.amuletObtained;
		Random.pushGenerator(0x5350534D4F42434EL);
		try {
			for (boolean amulet : new boolean[]{false, true}) {
				Statistics.amuletObtained = amulet;
				for (int depth = 1; depth < 25; depth++) {
					Dungeon.depth = depth;
					SpsRegularLevel level = newLevelForDepth(depth);
					for (int sample = 0; sample < 20; sample++) {
						int count = level.initialMobCount();
						int base = amulet ? 10 + (5 - depth % 5)
								: depth < 5 ? 10 + depth : 15 + depth % 3;
						check(count >= base && count <= base + 2,
								(depth - 1) / 5, sample, "旧版初始怪物数量越界");
					}
				}
			}
		} finally {
			Statistics.amuletObtained = previousAmulet;
			Random.popGenerator();
		}
	}

	private static void validateLegacyRegionArmor() {
		Class<?>[] types = {SewerLevel.class, PrisonLevel.class, CavesLevel.class,
				CityLevel.class, HallsLevel.class};
		int[] depths = {2, 7, 12, 17, 22};
		int[] multipliers = {0, 0, 5, 10, 15};
		for (int region = 0; region < types.length; region++) {
			Dungeon.depth = depths[region];
			SpsRegularLevel level = newLevel(types[region]);
			level.mobs = new HashSet<>();
			Mob mob = new Rat();
			level.applyLegacyInitialMobTraits(mob);
			check(mob.spsOriginalGeneration, region, -4, "初始怪物未标记为旧版原生怪物");
			ShieldArmor physical = mob.buff(ShieldArmor.class);
			MagicArmor magic = mob.buff(MagicArmor.class);
			GlassShield glass = mob.buff(GlassShield.class);
			if (multipliers[region] == 0) {
				check(physical == null && magic == null && glass == null,
						region, -4, "下水道或监狱错误获得区域护甲");
			} else {
				int expected = depths[region] * multipliers[region];
				check(physical != null && physical.level() == expected,
						region, -4, "物理护甲数值错误");
				check(magic != null && magic.level() == expected,
						region, -4, "魔法护甲数值错误");
				check((region == 4 && glass != null && glass.turns() == 1)
						|| (region != 4 && glass == null), region, -4, "玻璃盾层数错误");
			}
			Mob questNpc = new Rat();
			level.mobs.add(questNpc);
			level.markSpsOriginalMobs();
			check(!questNpc.spsOriginalGeneration, region, -4, "任务角色被误标记为初始怪物");
		}
	}

	private static void validateLegacyBranchDepthScaling() {
		boolean previousAmulet = Statistics.amuletObtained;
		Dungeon.depth = pd.items.quest.AdventureJournal.anchorDepth(9);
		Dungeon.branch = pd.items.quest.AdventureJournal.branchFor(9);
		check(Dungeon.legacyDepth() == 35, 0, -40, "异界随机层没有恢复旧版35层数值深度");
		Statistics.amuletObtained = false;
		Random.pushGenerator(0x5350534252414E43L);
		try {
			SewerLevel countLevel = new SewerLevel();
			for (int sample = 0; sample < 100; sample++) {
				int count = countLevel.initialMobCount();
				check(count >= 17 && count <= 19, 0, sample,
						"异界随机层初始怪物数量未按35层计算");
			}

			boolean sawDeepTrapCount = false;
			for (int sample = 0; sample < 400; sample++) {
				int traps = countLevel.nTraps();
				check(traps >= 13 && traps <= 37, 0, sample,
						"异界随机层陷阱数量越过旧版35层范围");
				if (traps > 22) sawDeepTrapCount = true;
			}
			check(sawDeepTrapCount, 0, -40, "异界随机层陷阱仍按入口锚点4层生成");

			CavesLevel armorLevel = new CavesLevel();
			Mob mob = new Rat();
			armorLevel.applyLegacyInitialMobTraits(mob);
			check(mob.buff(ShieldArmor.class) != null
						&& mob.buff(ShieldArmor.class).level() == 175
						&& mob.buff(MagicArmor.class) != null
						&& mob.buff(MagicArmor.class).level() == 175,
					0, -40, "异界随机层区域护甲仍按入口锚点成长");
		} finally {
			Random.popGenerator();
			Statistics.amuletObtained = previousAmulet;
			Dungeon.depth = 1;
			Dungeon.branch = 0;
		}
	}

	private static void validateLegacyItemGeneration() {
		Level previousLevel = Dungeon.level;
		Hero previousHero = Dungeon.hero;
		boolean foundLockedChest = false;
		boolean foundMonsterBox = false;
		boolean foundOrdinaryMimic = false;
		Random.pushGenerator(0x5350534954454D53L);
		try {
			Dungeon.hero = new Hero();
			Dungeon.depth = 2;
			Dungeon.branch = 0;
			for (int sample = 0; sample < 200; sample++) {
				TestSewerLevel level = new TestSewerLevel();
				check(buildWithRetries(level), 0, -20 - sample, "物资测试地图构建失败");
				level.buildFlagMaps();
				Dungeon.level = level;
				int dustBefore = countHeapType(level, Heap.Type.E_DUST);
				int webBefore = countHeapType(level, Heap.Type.M_WEB);
				int lockedBefore = countHeapType(level, Heap.Type.LOCKED_CHEST);
				int monsterBefore = countHeapType(level, Heap.Type.G_MIMIC);
				int mimicBefore = countHeapType(level, Heap.Type.MIMIC);
				int keysBefore = countItems(level, GoldenKey.class);
				level.queue(new StrBottle());
				level.generateLegacyItems();

				check(countHeapType(level, Heap.Type.E_DUST) - dustBefore == 10,
						0, -20 - sample, "普通层没有生成恰好10个尘土堆");
				check(countHeapType(level, Heap.Type.M_WEB) - webBefore == 3,
						0, -20 - sample, "普通层没有生成恰好3个蛛网堆");
				int locked = countHeapType(level, Heap.Type.LOCKED_CHEST) - lockedBefore;
				int monster = countHeapType(level, Heap.Type.G_MIMIC) - monsterBefore;
				int ordinaryMimics = countHeapType(level, Heap.Type.MIMIC) - mimicBefore;
				check(locked + monster == 1, 0, -20 - sample,
						"高级奖励没有严格生成锁箱或怪物箱之一");
				int newKeys = countItems(level, GoldenKey.class) - keysBefore;
				check(monster == 1 || newKeys >= 1,
						0, -20 - sample, "锁箱奖励没有同时生成金钥匙");
				check(countItems(level, StrBottle.class) == 1,
						0, -20 - sample, "旧版特殊队列物品没有进入地图");
				foundLockedChest |= locked == 1;
				foundMonsterBox |= monster == 1;
				if (ordinaryMimics > 0) {
					foundOrdinaryMimic = true;
					Heap heap = firstHeap(level, Heap.Type.MIMIC);
					check(heap != null && heap.peek() != null, 0, -20 - sample,
							"普通拟态箱没有保留原始奖励");
					Bundle saved = new Bundle();
					heap.storeInBundle(saved);
					Heap restored = new Heap();
					restored.restoreFromBundle(saved);
					check(restored.type == Heap.Type.MIMIC && restored.peek() != null,
							0, -20 - sample, "普通拟态箱存档往返丢失类型或奖励");
				}
				if (monster == 1) {
					Heap heap = firstHeap(level, Heap.Type.G_MIMIC);
					check(heap != null && heap.peek() != null, 0, -20 - sample,
							"怪物箱没有保留旧版高级奖励");
					Bundle saved = new Bundle();
					heap.storeInBundle(saved);
					Heap restored = new Heap();
					restored.restoreFromBundle(saved);
					check(restored.type == Heap.Type.G_MIMIC && restored.peek() != null,
							0, -20 - sample, "怪物箱存档往返丢失类型或奖励");
					int boxes = countMobs(level, MonsterBox.class);
					heap.open(Dungeon.hero);
					check(heap.type == Heap.Type.HEAP && heap.peek() != null
							&& countMobs(level, MonsterBox.class) == boxes + 1,
							0, -20 - sample, "打开伪装锁箱没有生成怪物箱并留下奖励");
				}
			}
			check(foundLockedChest && foundMonsterBox, 0, -220,
					"200次物资生成没有覆盖锁箱与怪物箱两条分支");
			check(foundOrdinaryMimic, 0, -221,
					"200次物资生成没有覆盖普通拟态箱分支");
		} finally {
			Dungeon.level = previousLevel;
			Dungeon.hero = previousHero;
			Random.popGenerator();
		}
	}

	private static void validateLegacyRemains() {
		Level previousLevel = Dungeon.level;
		Hero previousHero = Dungeon.hero;
		try {
			Dungeon.hero = new Hero();
			Dungeon.depth = 2;
			Dungeon.branch = 0;
			TestSewerLevel level = new TestSewerLevel();
			check(buildWithRetries(level), 0, -221, "遗骸测试地图构建失败");
			level.buildFlagMaps();
			Dungeon.level = level;

			ArrayList<Integer> cells = new ArrayList<>();
			for (int cell = 0; cell < level.length() && cells.size() < 2; cell++) {
				if (level.passable[cell] && level.heaps.get(cell) == null
						&& pd.actors.Actor.findChar(cell) == null) {
					cells.add(cell);
				}
			}
			check(cells.size() == 2, 0, -221, "遗骸测试找不到两个合法格子");
			Heap skeleton = level.drop(new Food(), cells.get(0));
			skeleton.type = Heap.Type.SKELETON;
			skeleton.haunted = true;
			Heap remains = level.drop(new Food(), cells.get(1));
			remains.type = Heap.Type.REMAINS;

			int redBefore = countMobs(level, RedWraith.class);
			int ordinaryBefore = countExactMobs(level, Wraith.class);
			skeleton.open(Dungeon.hero);
			remains.open(Dungeon.hero);
			check(skeleton.type == Heap.Type.HEAP && remains.type == Heap.Type.HEAP,
					0, -221, "骨堆或遗骸打开后没有转为普通物品堆");
			check(countMobs(level, RedWraith.class) == redBefore + 2,
					0, -221, "骨堆与遗骸没有各生成一只红色怨灵");
			check(countExactMobs(level, Wraith.class) == ordinaryBefore,
					0, -221, "诅咒骨堆额外生成了现代普通怨灵");
		} finally {
			Dungeon.level = previousLevel;
			Dungeon.hero = previousHero;
		}
	}

	private static void validateLegacyPrebuildSupplies() {
		Level previousLevel = Dungeon.level;
		Hero previousHero = Dungeon.hero;
		long previousSeed = Dungeon.seed;
		try {
			Dungeon.hero = new Hero();
			Dungeon.depth = 4;
			Dungeon.branch = 0;
			Dungeon.seed = 0x535053535550504CL;
			Dungeon.LimitedDrops.reset();
			CaptureSewerLevel level = new CaptureSewerLevel();
			Dungeon.level = level;
			level.create();
			check(countQueued(level.captured, Food.class) == 2, 0, -221,
					"普通层预生成物资不是两份旧版食物");
			check(countQueued(level.captured, ScrollOfUpgrade.class) == 1, 0, -221,
					"普通层没有固定生成一张升级卷轴");
			int stylus = countQueued(level.captured, Stylus.class);
			int weightstone = countQueued(level.captured, Weightstone.class);
			check(stylus == weightstone && stylus <= 1, 0, -221,
					"刻印笔与磨刀石没有按旧版成对生成");
			check(countQueued(level.captured, StrBottle.class) == 1, 0, -221,
					"应生成力量瓶的楼层仍使用现代力量药剂或没有奖励");
		} finally {
			Dungeon.LimitedDrops.reset();
			Dungeon.level = previousLevel;
			Dungeon.hero = previousHero;
			Dungeon.seed = previousSeed;
		}
	}

	private static SpsRegularLevel newLevelForDepth(int depth) {
		if (depth < 6) return new SewerLevel();
		if (depth < 11) return new PrisonLevel();
		if (depth < 16) return new CavesLevel();
		if (depth < 21) return new CityLevel();
		return new HallsLevel();
	}

	private static void validateExitGuardDepthTable() {
		Random.pushGenerator(0x5350534558495447L);
		try {
			for (int depth = 1; depth <= 25; depth++) {
				Dungeon.depth = depth;
				for (int sample = 0; sample < 200; sample++) {
					Mob guard = SpsExitMobs.randomForDepth(depth);
					boolean valid;
					switch (depth) {
						case 2: valid = guard instanceof SpsExitMobs.GuardAlbino; break;
						case 3: case 4: valid = guard instanceof SpsExitMobs.GuardAlbino
								|| guard instanceof SpsExitMobs.GuardVagrant; break;
						case 7: valid = guard instanceof SpsExitMobs.GuardBandit
								|| guard instanceof SpsExitMobs.GuardVagrant; break;
						case 8: case 9: valid = guard instanceof SpsExitMobs.GuardBandit
								|| guard instanceof SpsExitMobs.GuardBamboo; break;
						case 12: case 13: case 14: valid = guard instanceof BombBug
								|| guard instanceof SpsExitMobs.GuardShielded; break;
						case 17: case 18: case 19: valid = guard instanceof SpsExitMobs.GuardSenior; break;
						case 22: valid = guard instanceof SpsExitMobs.GuardFireSuccubus; break;
						case 23: case 24: valid = guard instanceof SpsExitMobs.GuardAcidic
								|| guard instanceof SpsExitMobs.GuardFireSuccubus; break;
						default: valid = guard == null;
					}
					check(valid, Math.max(0, (depth - 1) / 5), sample, "出口守卫深度表错误");
				}
			}
		} finally {
			Random.popGenerator();
		}
	}

	private static void validateExitProtectionLifecycle() {
		Level previousLevel = Dungeon.level;
		Dungeon.depth = 2;
		Random.pushGenerator(0x53505350524F5445L);
		try {
			SewerLevel level = new SewerLevel();
			check(buildWithRetries(level), 0, -5, "出口保护测试地图构建失败");
			level.buildFlagMaps();
			Dungeon.level = level;
			Mob guard = null;
			for (Mob mob : level.mobs) if (mob instanceof SpsExitMobs.ExitGuard) guard = mob;
			check(guard != null, 0, -5, "出口保护测试缺少守卫");
			int hp = guard.HP;
			ShieldArmor shield = guard.buff(ShieldArmor.class);
			check(shield != null, 0, -5, "出口守卫缺少物理护甲 buff");
			int physical = shield.level();
			guard.damage(hp, new Rat());
			check(guard.HP == hp, 0, -5, "出口守卫首次伤害没有完全抵消");
			check(guard.buff(ExProtect.class) == null && guard.buff(BoxStar.class) != null,
					0, -5, "出口保护没有转换为三回合无敌");
			check(guard.paralysed == 0, 0, -5, "出口保护解除后仍处于麻痹");
			check(guard.buff(ShieldArmor.class).level() == physical,
					0, -5, "首次抵消错误消耗了物理护甲");
		} finally {
			Dungeon.level = previousLevel;
			Random.popGenerator();
		}
	}

	private static void validateEnhancedPlantSuppression() {
		Random.pushGenerator(0x535053504C414E54L);
		try {
			Dungeon.depth = 6;
			Dungeon.branch = 0;
			PrisonLevel shop = new PrisonLevel();
			check(buildWithRetries(shop), 1, -6, "商店层植物测试地图构建失败");
			check(countEnhancedPlants(shop) == 0, 1, -6, "商店层错误生成强化植物");

			Dungeon.depth = 7;
			Dungeon.branch = 1;
			PrisonLevel branch = new PrisonLevel();
			check(buildWithRetries(branch), 1, -7, "支线植物测试地图构建失败");
			check(countEnhancedPlants(branch) == 0, 1, -7, "支线错误生成强化植物");
		} finally {
			Dungeon.branch = 0;
			Random.popGenerator();
		}
	}

	private static int countEnhancedPlants(Level level) {
		int count = 0;
		for (Plant plant : level.plants.valueList()) if (plant instanceof SpsFruitBush) count++;
		return count;
	}

	private static void validateLegacyBlacksmithRoom(CavesLevel level) {
		SpsBspLayout.Room smithy = null;
		for (SpsBspLayout.Room room : level.legacyLayout.rooms) {
			if (room.type == SpsBspLayout.Type.BLACKSMITH) {
				smithy = room;
				break;
			}
		}
		check(smithy != null && Blacksmith.Quest.isLegacy(), 2, 0,
				"首个洞穴普通层没有生成旧版铁匠房");
		check(countMobs(level, Blacksmith.class) == 1
				&& countMobs(level, Blacksmith2.class) == 1,
				2, 0, "旧版铁匠房没有同时生成铁匠和焊工");

		int equipment = 0;
		int fireTraps = 0;
		for (int y = smithy.top + 1; y < smithy.bottom; y++) {
			for (int x = smithy.left + 1; x < smithy.right; x++) {
				int cell = x + y * level.width();
				boolean inner = x >= smithy.left + 2 && x <= smithy.right - 2
						&& y >= smithy.top + 2 && y <= smithy.bottom - 2;
				if (inner) {
					check(level.map[cell] == Terrain.EMPTY_SP, 2, 0,
							"旧版铁匠房第二内圈不是特殊地面");
				} else if (level.map[cell] == Terrain.TRAP) {
					check(level.traps.get(cell) instanceof FireDamageTrap, 2, 0,
							"旧版铁匠房陷阱环不是可见火焰陷阱");
					fireTraps++;
				}
				if (level.heaps.get(cell) != null) {
					check(level.map[cell] == Terrain.TRAP, 2, 0,
							"旧版铁匠房装备没有放在陷阱环上");
					equipment += level.heaps.get(cell).size();
				}
			}
		}
		check(equipment == 2 && fireTraps > 0, 2, 0,
				"旧版铁匠房装备数量或火焰陷阱环错误");
		for (SpsBspLayout.Door door : smithy.connected.values()) {
			check(door != null && door.type == SpsBspLayout.Door.Type.UNLOCKED,
					2, 0, "旧版铁匠房门没有设为未上锁");
		}

		Bundle saved = new Bundle();
		Blacksmith.Quest.storeInBundle(saved);
		Bundle node = saved.getBundle("blacksmith");
		check(node.getBoolean("spawned") && node.getBoolean("old_quest")
				&& !node.getBoolean("reforged"), 2, 0,
				"旧版铁匠任务存档模式错误");
	}

	private static void validateLegacyBlacksmithRules() {
		Item first = new Mace().identify(false).level(0);
		Item material = new LeatherArmor().identify(false).level(2);
		check(Blacksmith.verifyLegacy(first, material) == null, 2, -6,
				"旧版铁匠错误要求两件物品类型相同");
		material.level(0);
		check(Blacksmith.verifyLegacy(first, material) != null, 2, -6,
				"旧版铁匠接受了未强化的第二件物品");
		material.level(2);
		material.cursed = true;
		check(Blacksmith.verifyLegacy(first, material) != null, 2, -6,
				"旧版铁匠接受了诅咒物品");

		LeatherArmor armor = (LeatherArmor)new LeatherArmor().identify(false);
		Mace weapon = (Mace)new Mace().identify(false);
		RingOfAccuracy ring = (RingOfAccuracy)new RingOfAccuracy().identify(false);
		WandOfMagicMissile wand = (WandOfMagicMissile)new WandOfMagicMissile().identify(false);
		check(Blacksmith2.verify(armor, new AdamantArmor()) == null
				&& Blacksmith2.verify(weapon, new AdamantWeapon()) == null
				&& Blacksmith2.verify(ring, new AdamantRing()) == null
				&& Blacksmith2.verify(wand, new AdamantWand()) == null,
				2, -7, "巨魔焊工拒绝了正确的精金组件");
		check(Blacksmith2.verify(armor, new AdamantWeapon()) != null, 2, -7,
				"巨魔焊工接受了不匹配的精金组件");
		armor.reinforce();
		check(Blacksmith2.verify(armor, new AdamantArmor()) != null, 2, -7,
				"巨魔焊工接受了已强化物品");

		Bundle legacyNode = new Bundle();
		legacyNode.put("spawned", true);
		legacyNode.put("given", true);
		legacyNode.put("completed", true);
		legacyNode.put("reforged", false);
		Bundle legacyRoot = new Bundle();
		legacyRoot.put("blacksmith", legacyNode);
		Blacksmith.Quest.restoreFromBundle(legacyRoot);
		check(Blacksmith.Quest.isLegacy() && Blacksmith.Quest.completed()
				&& Blacksmith.Quest.rewardsAvailable(), 2, -7,
				"旧版铁匠存档没有按旧任务恢复");

		Triforce triforce = new Triforce();
		check(triforce.actions(Dungeon.hero).contains(Triforce.AC_PORT)
				&& !triforce.isUpgradable(), 2, -7,
				"合成后的起源三角缺少传送动作或可被错误强化");
		Blacksmith.Quest.reset();
	}

	private static void validateLegacyCityQuestSpawns() {
		Level previousLevel = Dungeon.level;
		Random.pushGenerator(0x535053494D505350L);
		try {
			Imp.Quest.reset();
			CityLevel first = populatedCity(17);
			check(countMobs(first, Imp.class) == 1 && Imp.Quest.isOld(), 3, -8,
					"第17层没有生成旧版小恶魔任务");

			Bundle saved = new Bundle();
			Imp.Quest.storeInBundle(saved);
			Bundle node = saved.getBundle("demon");
			Item reward = (Item)node.get("reward");
			check(node.getBoolean("spawned") && node.getBoolean("old_quest")
					&& reward != null && reward.level() == 2 && reward.cursed,
					3, -8, "小恶魔旧任务状态或+2诅咒戒指奖励错误");
			check(Imp.Quest.legacyTokenGoal() == (node.getBoolean("alternative") ? 8 : 6),
					3, -8, "小恶魔令牌需求没有恢复为8/6枚");

			CityLevel second = populatedCity(18);
			check(countMobs(second, Imp.class) == 0, 3, -9, "小恶魔任务在后续都市层重复生成");

			Imp.Quest.reset();
			CityLevel finalCity = populatedCity(19);
			check(countMobs(finalCity, Imp.class) == 1 && countMobs(finalCity, GoldThief.class) == 1,
					3, -10, "第19层没有同时生成小恶魔和黄金盗贼");
		} finally {
			Imp.Quest.reset();
			Dungeon.level = previousLevel;
			Random.popGenerator();
		}
	}

	private static void validateLegacyImpTokenDrops() {
		Level previousLevel = Dungeon.level;
		try {
			TestCityLevel level = populatedCity(17);
			level.heaps.clear();
			Dungeon.level = level;

			restoreLegacyImpQuest(false, true);
			Golem golem = new Golem();
			golem.pos = 10;
			Imp.Quest.oldProcess(golem);
			check(countItems(level, DwarfToken.class) == 1, 3, -11,
					"魔像任务击杀没有掉落矮人令牌");
			Imp.Quest.oldProcess(new Monk());
			check(countItems(level, DwarfToken.class) == 1, 3, -11,
					"魔像任务错误接受僧侣击杀");

			level.heaps.clear();
			restoreLegacyImpQuest(true, true);
			Monk monk = new Monk();
			monk.pos = 20;
			Imp.Quest.oldProcess(monk);
			check(countItems(level, DwarfToken.class) == 1, 3, -12,
					"僧侣任务击杀没有掉落矮人令牌");
		} finally {
			Imp.Quest.reset();
			Dungeon.level = previousLevel;
		}
	}

	private static TestCityLevel populatedCity(int depth) {
		Dungeon.depth = depth;
		Dungeon.branch = 0;
		TestCityLevel level = new TestCityLevel();
		prepare(level);
		level.setSize(9, 9);
		Arrays.fill(level.map, Terrain.WALL);
		for (int y = 1; y < 8; y++) {
			for (int x = 1; x < 8; x++) level.map[x + y * 9] = Terrain.EMPTY;
		}
		level.buildFlagMaps();
		Dungeon.level = level;
		level.createLegacyQuestActors();
		return level;
	}

	private static final class TestCityLevel extends CityLevel {
		private int nextRespawnCell = 10;

		@Override
		public int randomRespawnCell(pd.actors.Char ch) {
			return nextRespawnCell++;
		}

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

	private static final class TestSewerLevel extends SewerLevel {
		void queue(Item item) {
			itemsToSpawn.add(item);
		}

		void generateLegacyItems() {
			createItems();
		}

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

	private static final class CaptureSewerLevel extends SewerLevel {
		ArrayList<Item> captured;

		@Override
		protected void createItems() {
			captured = new ArrayList<>(itemsToSpawn);
		}

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

	private static void restoreLegacyImpQuest(boolean alternative, boolean given) {
		Bundle node = new Bundle();
		node.put("spawned", true);
		node.put("old_quest", true);
		node.put("alternative", alternative);
		node.put("given", given);
		node.put("completed", false);
		Bundle root = new Bundle();
		root.put("demon", node);
		Imp.Quest.restoreFromBundle(root);
	}

	private static int countMobs(Level level, Class<?> type) {
		int count = 0;
		for (Mob mob : level.mobs) if (type.isInstance(mob)) count++;
		return count;
	}

	private static int countExactMobs(Level level, Class<?> type) {
		int count = 0;
		for (Mob mob : level.mobs) if (mob.getClass() == type) count++;
		return count;
	}

	private static int countItems(Level level, Class<?> type) {
		int count = 0;
		for (Heap heap : level.heaps.valueList()) {
			for (Item item : heap.items) if (type.isInstance(item)) count += item.quantity();
		}
		return count;
	}

	private static int countHeapType(Level level, Heap.Type type) {
		int count = 0;
		for (Heap heap : level.heaps.valueList()) if (heap.type == type) count++;
		return count;
	}

	private static Heap firstHeap(Level level, Heap.Type type) {
		for (Heap heap : level.heaps.valueList()) if (heap.type == type) return heap;
		return null;
	}

	private static int countQueued(ArrayList<Item> items, Class<?> type) {
		int count = 0;
		for (Item item : items) if (type.isInstance(item)) count += item.quantity();
		return count;
	}

	private static void check(boolean condition, int region, int seed, String message) {
		if (!condition) throw new AssertionError("区域" + region + "，种子" + seed + "：" + message);
	}

	private SpsRegularLevelTest() {
	}
}
