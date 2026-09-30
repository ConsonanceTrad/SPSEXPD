package pd.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.mobs.AlbinoPiranha;
import pd.actors.mobs.Bat;
import pd.actors.mobs.BrokenRobot;
import pd.actors.mobs.BrownBat;
import pd.actors.mobs.Brute;
import pd.actors.mobs.Crab;
import pd.actors.mobs.DemonFlower;
import pd.actors.mobs.DemonGoo;
import pd.actors.mobs.DragonRider;
import pd.actors.mobs.DustElement;
import pd.actors.mobs.DwarfLich;
import pd.actors.mobs.Eye;
import pd.actors.mobs.Fiend;
import pd.actors.mobs.FireElemental;
import pd.actors.mobs.FishProtector;
import pd.actors.mobs.FlyingProtector;
import pd.actors.mobs.ForestProtector;
import pd.actors.mobs.Gnoll;
import pd.actors.mobs.GnollArcher;
import pd.actors.mobs.GnollShaman;
import pd.actors.mobs.GoldThief;
import pd.actors.mobs.Golem;
import pd.actors.mobs.GraveProtector;
import pd.actors.mobs.Guard;
import pd.actors.mobs.IceBall;
import pd.actors.mobs.IceBug;
import pd.actors.mobs.LiveMoss;
import pd.actors.mobs.ManySkeleton;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Monk;
import pd.actors.mobs.MonsterBox;
import pd.actors.mobs.MossySkeleton;
import pd.actors.mobs.Musketeer;
import pd.actors.mobs.PatrolUAV;
import pd.actors.mobs.Rat;
import pd.actors.mobs.SandMob;
import pd.actors.mobs.Scorpio;
import pd.actors.mobs.Skeleton;
import pd.actors.mobs.SpiderBot;
import pd.actors.mobs.Spinner;
import pd.actors.mobs.SpsChallengeMobPool;
import pd.actors.mobs.Succubus;
import pd.actors.mobs.Sufferer;
import pd.actors.mobs.Thief;
import pd.actors.mobs.ThiefImp;
import pd.actors.mobs.TimeKeeper;
import pd.actors.mobs.VaultProtector;
import pd.actors.mobs.Warlock;
import pd.items.Dewdrop;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.bombs.FishingBomb;
import pd.items.bombs.LightBomb;
import pd.items.quest.ChallengeJournal;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/** Fixed-seed checks for the five SPS-PD challenge-book region arenas. */
public final class SpsChallengeLevelsTest {

	private SpsChallengeLevelsTest() {
	}

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Dungeon.hero = null;
		Heap mergeProbe = new Heap();
		mergeProbe.drop(new Dewdrop());
		mergeProbe.drop(new Dewdrop());
		check(mergeProbe.items.size() == 1 && mergeProbe.peek().quantity() == 2,
				"通用物品堆未正确合并同类露珠，条目=" + mergeProbe.items.size()
						+ "，数量=" + (mergeProbe.peek() == null ? -1 : mergeProbe.peek().quantity()));

		int sewerShrubs = 0;
		int prisonPlants = 0;
		int caveWater = 0;
		for (int seed = 0; seed < 250; seed++) {
			SewerChallengeLevel sewer = build(new SewerChallengeLevel(), 0, seed);
			PrisonChallengeLevel prison = build(new RecordingPrisonChallengeLevel(), 1, seed);
			CaveChallengeLevel cave = build(new RecordingCaveChallengeLevel(), 2, seed);
			CityChallengeLevel city = build(new RecordingCityChallengeLevel(), 3, seed);
			RecordingIceChallengeLevel ice = build(new RecordingIceChallengeLevel(), 4, seed);

			sewerShrubs += countTerrain(sewer, Terrain.SHRUB);
			prisonPlants += countTerrain(prison, Terrain.SHRUB)
					+ countTerrain(prison, Terrain.GRASS) + countTerrain(prison, Terrain.HIGH_GRASS);
			caveWater += countTerrain(cave, Terrain.WATER);
			checkMobPool(sewer, 10, GnollArcher.class, ForestProtector.class, Brute.class);
			checkMobPool(prison, 16, MossySkeleton.class, GraveProtector.class,
					ManySkeleton.class);
			checkMobPool(cave, 30, AlbinoPiranha.class, FishProtector.class, Crab.class);
			checkMobPool(city, 16, GoldThief.class, VaultProtector.class, Succubus.class);
			checkMobPool(ice, 10, IceBall.class);
			validateChallengeMobStats(sewer, 0, 90);
			validateChallengeMobStats(prison, 1, 27);
			validateChallengeMobStats(cave, 2, 28);
			validateChallengeMobStats(city, 3, 29);
			for (Mob mob : cave.mobs()) {
				check(cave.map[mob.pos] == Terrain.WATER,
						"洞窟挑战敌人必须生成在水中：" + mob.getClass().getSimpleName());
			}
			check(countItems(prison, LightBomb.class) == 5,
					"监狱挑战宝箱必须装有5枚圣光炸弹");
			check(countHeaps(prison, Heap.Type.CHEST) == 1,
					"监狱挑战必须只有1个炸弹宝箱");
			check(countItems(cave, FishingBomb.class) == 5,
					"洞窟挑战宝箱必须装有5枚鱼饵炸弹");
			Heap fishingChest = cave.heaps.get(cave.legacyEntranceCell() + 1);
			check(fishingChest != null && fishingChest.type == Heap.Type.CHEST,
					"洞窟挑战鱼饵炸弹宝箱必须位于入口右侧");
			check(countItems(city, Gold.class) >= 100 && countItems(city, Gold.class) <= 290,
					"城市挑战必须生成10堆、每堆10至29金币");
			int dewdrops = countItems(ice, Dewdrop.class);
			check(ice.recordedQuantity == 10, "冰原挑战掉落循环未执行10次");
			check(dewdrops == 10, "冰原挑战露珠堆合并后数量错误，实际=" + dewdrops);
		}

		check(sewerShrubs > 0, "下水道挑战缺少旧版灌木装饰");
		check(prisonPlants > 0, "监狱挑战缺少旧版植被装饰");
		check(caveWater > 250 * 100, "洞窟挑战未恢复大面积水域");
		Class<? extends Mob>[] iceBallPool = SpsChallengeMobPool.iceBallSpawnTypes();
		check(iceBallPool.length == 50, "冰球死亡生成池必须包含旧版50种敌人，实际=" + iceBallPool.length);
		HashSet<Class<?>> iceBallTypes = new HashSet<>();
		for (Class<? extends Mob> type : iceBallPool) iceBallTypes.add(type);
		check(iceBallTypes.size() == 50 && iceBallTypes.contains(MonsterBox.class),
				"冰球死亡生成池缺少怪物盒或含重复类型");
		validateIceBallSpawnStats(iceBallPool);
		System.out.println("SPS区域挑战地图测试通过：五张48x48旧版竞技场、中央入口、封闭北端、装饰、水域、金币、露珠及冰球50类怪物池均正常。");
	}

	private static void validateIceBallSpawnStats(Class<? extends Mob>[] pool) {
		Dungeon.depth = ChallengeJournal.anchorDepth(4);
		Dungeon.branch = ChallengeJournal.branchFor(4);
		check(Mob.legacyDungeonDepth() == 30, "冰原挑战必须使用旧版有效深度30");
		Random.pushGenerator(0x49434542414C4C50L);
		try {
			for (int seed = 0; seed < 64; seed++) {
				for (Class<? extends Mob> type : pool) {
					Mob mob = Reflection.newInstance(type);
					check(mob != null && mob.HT > 0,
							"冰球怪物无法安全构造：" + type.getSimpleName());
					validateIceBallMobStats(mob);
				}
			}
		} finally {
			Random.popGenerator();
		}
	}

	private static void validateIceBallMobStats(Mob mob) {
		if (mob instanceof Rat) {
			checkLegacyStats(mob, 70, 130, 18, 35, 1, 35, 0, 1);
		} else if (mob instanceof BrownBat) {
			checkLegacyStats(mob, 20, 20, 1, 35, 1, 4, 1, 1);
		} else if (mob instanceof DustElement) {
			checkLegacyStats(mob, 65, 125, 19, 41, 2, 35, 0, 2);
		} else if (mob instanceof LiveMoss) {
			checkLegacyStats(mob, 80, 140, 20, 42, 3, 36, 0, 4);
		} else if (mob instanceof Crab) {
			checkLegacyStats(mob, 80, 140, 20, 42, 3, 36, 0, 4);
		} else if (mob instanceof PatrolUAV) {
			checkLegacyStats(mob, 80, 140, 15, 5, 4, 7, 5, 10);
		} else if (mob instanceof Thief) {
			checkLegacyStats(mob, 170, 230, 38, 12, 1, 37, 0, 3);
		} else if (mob instanceof Gnoll) {
			checkLegacyStats(mob, 160, 280, 24, 42, 40, 50, 5, 8);
		} else if (mob instanceof Guard) {
			checkLegacyStats(mob, 165, 285, 24, 42, 42, 80, 5, 10);
		} else if (mob instanceof pd.actors.mobs.Assassin) {
			checkLegacyStats(mob, 90, 105, 15, 25, 10, 23, 0, 5);
		} else if (mob instanceof pd.actors.mobs.Zombie) {
			checkLegacyStats(mob, 160, 280, 24, 45, 40, 50, 3, 8);
		} else if (mob instanceof Bat) {
			checkLegacyStats(mob, 140, 230, 45, 46, 15, 52, 30, 30);
		} else if (mob instanceof Skeleton) {
			checkLegacyStats(mob, 140, 230, 45, 46, 15, 52, 2, 5);
		} else if (mob instanceof Brute) {
			checkLegacyStats(mob, 150, 180, 40, 25, 35, 55, 0, 10);
		} else if (mob instanceof TimeKeeper || mob instanceof GnollShaman) {
			checkLegacyStats(mob, 140, 230, 45, 46, 14, 50, 0, 4);
		} else if (mob instanceof Spinner) {
			checkLegacyStats(mob, 270, 330, 29, 50, 12, 56, 6, 10);
		} else if (mob instanceof BrokenRobot) {
			checkLegacyStats(mob, 240, 330, 35, 50, 0, 0, 0, 5);
		} else if (mob instanceof SandMob) {
			checkLegacyStats(mob, 150, 240, 35, 40, 17, 55, 0, 10);
		} else if (mob instanceof IceBug) {
			checkLegacyStats(mob, 140, 230, 45, 46, 15, 50, 2, 5);
		} else if (mob instanceof FireElemental) {
			checkLegacyStats(mob, 240, 330, 50, 40, 16, 35, 0, 15);
		} else if (mob instanceof Warlock) {
			checkLegacyStats(mob, 270, 330, 48, 55, 12, 54, 4, 8);
		} else if (mob instanceof Monk) {
			checkLegacyStats(mob, 250, 310, 45, 45, 22, 66, 2, 12);
		} else if (mob instanceof DragonRider) {
			checkLegacyStats(mob, 290, 350, 45, 51, 40, 50, 20, 40);
		} else if (mob instanceof Golem) {
			checkLegacyStats(mob, 300, 390, 33, 43, 50, 75, 10, 15);
		} else if (mob instanceof SpiderBot) {
			checkLegacyStats(mob, 270, 360, 40, 60, 45, 55, 0, 20);
		} else if (mob instanceof Musketeer) {
			checkLegacyStats(mob, 250, 310, 45, 50, 35, 90, 5, 10);
		} else if (mob instanceof DwarfLich) {
			checkLegacyStats(mob, 270, 330, 39, 51, 20, 32, 5, 15);
		} else if (mob instanceof Succubus) {
			checkLegacyStats(mob, 260, 320, 40, 55, 15, 55, 5, 10);
		} else if (mob instanceof Eye) {
			checkLegacyStats(mob, 320, 410, 35, 60, 5, 25, 20, 30);
		} else if (mob instanceof DemonGoo) {
			checkLegacyStats(mob, 420, 510, 25, 50, 45, 75, 10, 15);
		} else if (mob instanceof Scorpio) {
			checkLegacyStats(mob, 210, 270, 39, 51, 20, 82, 10, 20);
		} else if (mob instanceof ThiefImp) {
			checkLegacyStats(mob, 320, 410, 35, 60, 15, 55, 10, 20);
		} else if (mob instanceof DemonFlower) {
			checkLegacyStats(mob, 450, 450, 5, 50, 26, 37, 0, 5);
		} else if (mob instanceof Sufferer) {
			checkLegacyStats(mob, 300, 390, 31, 49, 25, 37, 0, 10);
		} else if (mob instanceof FlyingProtector) {
			checkLegacyStats(mob, 170, 170, 34, 39, 20, 30, 0, 30);
		} else if (mob instanceof Fiend) {
			checkLegacyStats(mob, 140, 230, 2, 50, 15, 30, 5, 10);
		}
	}

	private static void checkLegacyStats(Mob mob, int minHp, int maxHp, int defense,
			int attack, int minDamage, int maxDamage, int minDr, int maxDr) {
		String name = mob.getClass().getSimpleName();
		checkRange(mob.HT, minHp, maxHp, name + "生命");
		check(mob.defenseSkill == defense, name + "防御错误：" + mob.defenseSkill);
		check(mob.attackSkill(null) == attack, name + "命中错误：" + mob.attackSkill(null));
		checkRolls(mob, minDamage, maxDamage, minDr, maxDr);
	}

	private static <T extends SpsRegionChallengeLevel> T build(T level, int challenge, int seed) {
		Dungeon.depth = ChallengeJournal.anchorDepth(challenge);
		Dungeon.branch = ChallengeJournal.branchFor(challenge);
		Random.pushGenerator(0x5350534348414C4CL + 1000L * challenge + seed);
		try {
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			level.customTerrain = new ArrayList<>();
			level.customWalls = new ArrayList<>();
			level.mobs().clear();
			level.heaps = new SparseArray<>();
			level.blobs = new HashMap<>();
			level.plants = new SparseArray<>();
			level.traps = new SparseArray<>();
			check(level.build(), level.getClass().getSimpleName() + "构建失败");
			level.buildFlagMaps();
			Dungeon.level = level;
			level.createMobs();
			level.createItems();

			check(level.width() == 48 && level.height() == 48,
					level.getClass().getSimpleName() + "必须为48x48");
			int entrance = level.legacyEntranceCell();
			int x = entrance % level.width();
			int y = entrance / level.width();
			check(x >= 23 && x <= 24 && y >= 23 && y <= 24,
					level.getClass().getSimpleName() + "入口不在旧版中央四格");
			check(level.passable[entrance], level.getClass().getSimpleName() + "入口不可通行");
			check(level.map[level.legacyExitCell()] == Terrain.WALL,
					level.getClass().getSimpleName() + "北端封闭目标被改写");
			check(level.transitions.size() == 1 && level.entrance() == entrance,
					level.getClass().getSimpleName() + "支线出生过渡错误");
			check(level.mobs().findMob(entrance) == null,
					level.getClass().getSimpleName() + "敌人与入口重叠");
			return level;
		} finally {
			Random.popGenerator();
		}
	}

	private static int countTerrain(Level level, int terrain) {
		int result = 0;
		for (int value : level.map) if (value == terrain) result++;
		return result;
	}

	private static void validateChallengeMobStats(Level level, int challenge, int depth) {
		Dungeon.depth = ChallengeJournal.anchorDepth(challenge);
		Dungeon.branch = ChallengeJournal.branchFor(challenge);
		Dungeon.level = level;
		check(Mob.legacyDungeonDepth() == depth, "区域挑战旧版真实深度错误：" + depth);
		for (Mob mob : level.mobs()) {
			if (mob instanceof GnollArcher) {
				check(mob.HT == 25 && mob.defenseSkill == 5 && mob.attackSkill(null) == 30,
						"豺狼弓手属性偏离旧版");
				checkRolls(mob, 1, 8, 0, 0);
			} else if (mob instanceof ForestProtector) {
				check(mob.HT == 250 && mob.defenseSkill == 10 && mob.attackSkill(null) == 26,
						"森林守卫属性偏离旧版");
				checkRolls(mob, 5, 10, 5, 10);
			} else if (mob instanceof Brute) {
				checkRange(mob.HT, 120 + depth, 120 + depth * 2, "蛮兵生命");
				check(mob.defenseSkill == 10 + depth && mob.attackSkill(null) == 10 + depth / 2,
						"区域挑战蛮兵深度属性错误");
				checkRolls(mob, 5 + depth, 25 + depth, 0, 10);
			} else if (mob instanceof MossySkeleton) {
				checkRange(mob.HT, 160, 190, "苔藓骷髅生命");
				check(mob.defenseSkill == 20 && mob.attackSkill(null) == 28,
						"苔藓骷髅属性偏离旧版");
				checkRolls(mob, 20, 45, 10, 10);
			} else if (mob instanceof GraveProtector) {
				check(mob.HT == 350 && mob.defenseSkill == 15 && mob.attackSkill(null) == 20,
						"墓地守卫属性偏离旧版");
				checkRolls(mob, 8, 15, 0, 8);
			} else if (mob instanceof ManySkeleton) {
				check(mob.HT == 100 && mob.defenseSkill == 5
						&& mob.attackSkill(null) == 30 + depth / 2, "巨型骷髅深度属性错误");
				checkRolls(mob, 4, 7, 0, 0);
			} else if (mob instanceof AlbinoPiranha) {
				check(mob.HT == 20 && mob.defenseSkill == 40 && mob.attackSkill(null) == 20,
						"白化食人鱼属性偏离旧版");
				checkRolls(mob, 0, 4, 0, 0);
			} else if (mob instanceof FishProtector) {
				check(mob.HT == 300 && mob.defenseSkill == 25 && mob.attackSkill(null) == 25,
						"鱼群守卫属性偏离旧版");
				checkRolls(mob, 8, 10, 5, 15);
			} else if (mob instanceof Crab) {
				checkRange(mob.HT, 50 + depth, 50 + depth * 3, "螃蟹生命");
				check(mob.defenseSkill == 5 + depth / 2 && mob.attackSkill(null) == 12 + depth
						&& mob.EXP == 3, "螃蟹深度属性错误");
				checkRolls(mob, 3, 6 + depth, 0, 4);
			} else if (mob instanceof GoldThief) {
				check(mob.HT == 100 && mob.defenseSkill == 26 && mob.attackSkill(null) == 40,
						"黄金盗贼属性偏离旧版");
				checkRolls(mob, 20, 30, 14, 14);
			} else if (mob instanceof VaultProtector) {
				check(mob.HT == 400 && mob.defenseSkill == 10 && mob.attackSkill(null) == 40,
						"宝库守卫属性偏离旧版");
				checkRolls(mob, 8, 10, 5, 20);
			} else if (mob instanceof Succubus) {
				checkRange(mob.HT, 110 + depth * 5, 110 + depth * 7, "魅魔生命");
				check(mob.defenseSkill == 25 + depth / 2 && mob.attackSkill(null) == 40 + depth / 2
						&& mob.maxLvl == 35, "魅魔深度属性错误");
				checkRolls(mob, 15, 25 + depth, 5, 10);
			}
		}
	}

	private static void checkRolls(Mob mob, int minDamage, int maxDamage, int minDr, int maxDr) {
		for (int i = 0; i < 12; i++) {
			checkRange(mob.damageRoll(), minDamage, maxDamage, mob.getClass().getSimpleName() + "伤害");
			checkRange(mob.drRoll(), minDr, maxDr, mob.getClass().getSimpleName() + "减伤");
		}
	}

	private static void checkRange(int value, int min, int max, String label) {
		check(value >= min && value <= max, label + "越界：" + value + "，期望" + min + ".." + max);
	}

	private static int countItems(Level level, Class<? extends Item> type) {
		int result = 0;
		for (Heap heap : level.heaps.values()) {
			for (Item item : heap.items) if (type.isInstance(item)) result += item.quantity();
		}
		return result;
	}

	private static int countHeaps(Level level, Heap.Type type) {
		int result = 0;
		for (Heap heap : level.heaps.values()) if (heap.type == type) result++;
		return result;
	}

	@SafeVarargs
	private static void checkMobPool(Level level, int expected,
			Class<? extends Mob>... allowed) {
		check(level.mobs().size() == expected,
				level.getClass().getSimpleName() + "敌人数量错误，实际=" + level.mobs().size());
		for (Mob mob : level.mobs()) {
			boolean matched = false;
			for (Class<? extends Mob> type : allowed) {
				if (type.isInstance(mob)) {
					matched = true;
					break;
				}
			}
			check(matched, level.getClass().getSimpleName() + "生成了错误敌人："
					+ mob.getClass().getName());
		}
	}

	private static Heap recordDrop(Level level, Item item, int cell) {
		Heap heap = level.heaps.get(cell);
		if (heap == null) {
			heap = new Heap();
			heap.pos = cell;
			level.heaps.put(cell, heap);
		}
		heap.drop(item);
		return heap;
	}

	private static final class RecordingCityChallengeLevel extends CityChallengeLevel {
		@Override public Heap drop(Item item, int cell) { return recordDrop(this, item, cell); }
	}

	private static final class RecordingPrisonChallengeLevel extends PrisonChallengeLevel {
		@Override public Heap drop(Item item, int cell) { return recordDrop(this, item, cell); }
	}

	private static final class RecordingCaveChallengeLevel extends CaveChallengeLevel {
		@Override public Heap drop(Item item, int cell) { return recordDrop(this, item, cell); }
	}

	private static final class RecordingIceChallengeLevel extends IceChallengeLevel {
		private int recordedQuantity;
		@Override public Heap drop(Item item, int cell) {
			recordedQuantity += item.quantity();
			return recordDrop(this, item, cell);
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
