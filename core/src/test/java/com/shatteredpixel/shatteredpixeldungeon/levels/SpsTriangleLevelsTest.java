package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfDead;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfQuite;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenceUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.BlueWraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfLich;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FlyingProtector;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FireElemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldOrc;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Greatmoss;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.LevelChecker;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ManySkeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Orc;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Sentinel;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Zombie;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpsSewerMobs;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Weightstone;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfCourage;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfPower;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfWisdom;
import com.shatteredpixel.shatteredpixeldungeon.items.Vialupdater;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.ChallengeJournal;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.SpsBspLayout.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.SpsBspLayout.Type;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.shatteredpixel.shatteredpixeldungeon.plants.NutPlant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.ReNepenth;
import com.shatteredpixel.shatteredpixeldungeon.plants.Seedpod;
import com.shatteredpixel.shatteredpixeldungeon.plants.StarEater;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;
import com.watabou.utils.FileUtils;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/** Fixed-seed checks for the three SPS-PD Triforce trials. */
public final class SpsTriangleLevelsTest {

	private SpsTriangleLevelsTest() { }

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-triangle-levels" + java.io.File.separator);
		Game.version = "test";
		Dungeon.hero = new Hero();

		Set<Class<?>> couragePool = new HashSet<>();
		Set<Class<?>> powerPool = new HashSet<>();
		Set<Class<?>> wisdomPool = new HashSet<>();
		int wisdomChasms = 0;
		int wisdomEyes = 0;
		boolean courageDeadWeather = false;
		boolean wisdomQuietWeather = false;
		for (int seed = 0; seed < 250; seed++) {
			RecordingCourageLevel courage = build(new RecordingCourageLevel(), 5, seed);
			RecordingPowerLevel power = build(new RecordingPowerLevel(), 6, seed);
			RecordingWisdomLevel wisdom = build(new RecordingWisdomLevel(), 7, seed);

			checkReward(courage, TriforceOfCourage.class);
			checkReward(power, TriforceOfPower.class);
			checkReward(wisdom, TriforceOfWisdom.class);
			checkMobPool(courage, 16, 18, couragePool, BlueWraith.class, DwarfLich.class,
					Zombie.class, ManySkeleton.class);
			checkMobPool(power, 18, 20, powerPool, Orc.class, GoldOrc.class,
					Greatmoss.class, Brute.class);
			checkMobPool(wisdom, 15, 17, wisdomPool, FlyingProtector.class,
					FireElemental.class, LevelChecker.class,
					com.shatteredpixel.shatteredpixeldungeon.actors.mobs.PatrolUAV.class);
			validateLegacyMobStats(courage, 31);
			validateLegacyMobStats(power, 32);
			validateLegacyMobStats(wisdom, 33);
			validateTrialKeys(courage, 31);
			validateTrialKeys(power, 32);
			validateTrialKeys(wisdom, 33);
			check(countItems(power, Vialupdater.class) == 1,
					"力量试炼必须生成一个水袋升级器");
			check(countItems(wisdom, PotionOfLevitation.class) >= 1,
					"智慧试炼入口房必须生成浮空药剂");
			wisdomChasms += countTerrain(wisdom, Terrain.CHASM);
			wisdomEyes += countMobs(wisdom, com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye.class);
			check(courage.trialRoomCount() == 0, "勇气试炼不应生成普通或专属特殊房");
			check(power.trialRoomCount() == 1, "力量试炼必须恰好生成一个力量房");
			check(wisdom.trialRoomCount() == 1, "智慧试炼必须恰好生成一个智慧房");
			check(countPlants(power, Seedpod.class) + countPlants(power, NutPlant.class) == 1,
					"力量房必须生成一株种荚或坚果植物");
			check(countPlants(wisdom, StarEater.class) + countPlants(wisdom, BlandfruitBush.class)
					+ countPlants(wisdom, ReNepenth.class) == 1,
					"智慧房必须生成一株旧版特殊植物");
			check(countMobs(wisdom, Sentinel.class) == 1, "智慧房必须生成一个魔法守卫");
			validateLegacyStandardThemes(courage, power, wisdom);
			courageDeadWeather |= courage.blobs.get(WeatherOfDead.class) != null;
			wisdomQuietWeather |= wisdom.blobs.get(WeatherOfQuite.class) != null;
		}

		check(couragePool.size() == 4, "勇气试炼的四种旧版敌人未全部出现");
		check(powerPool.size() == 4, "力量试炼的四种旧版敌人未全部出现");
		check(wisdomPool.size() == 4, "智慧试炼的四种旧版敌人未全部出现");
		check(wisdomChasms > 0, "智慧试炼没有生成旧版悬空地形");
		check(wisdomEyes > 0, "智慧试炼悬空区域没有生成旧版邪眼伏兵");
		check(courageDeadWeather, "250张勇气试炼没有覆盖旧版死亡天气");
		check(wisdomQuietWeather, "250张智慧试炼没有覆盖旧版寂静天气");
		validateBruteEnrage();
		System.out.println("SPS三角试炼地图测试通过：三张48x48地图、旧版预排物资与层感、专属普通房、返回过渡、可达三角奖励、敌人池、力量升级器及智慧浮空区均正常。");
	}

	private static void validateLegacyMobStats(Level level, int depth) {
		int challenge = depth - 26;
		Dungeon.depth = ChallengeJournal.anchorDepth(challenge);
		Dungeon.branch = ChallengeJournal.branchFor(challenge);
		Dungeon.level = level;
		check(Mob.legacyDungeonDepth() == depth, "试炼没有使用旧版真实深度" + depth);
		for (Mob mob : level.mobs) {
			if (mob instanceof BlueWraith) {
				check(mob.HT == 250 && mob.defenseSkill == 24 && mob.attackSkill(Dungeon.hero) == 46,
						"蓝色怨灵属性偏离旧版");
				checkRolls(mob, 20, 90, 10, 25);
			} else if (mob instanceof DwarfLich) {
				checkRange(mob.HT, 120 + depth * 5, 120 + depth * 7, "矮人巫妖生命");
				check(mob.defenseSkill == 24 + depth / 2 && mob.attackSkill(Dungeon.hero) == 36 + depth / 2,
						"矮人巫妖深度属性错误");
				checkRolls(mob, 20, 32, 5, 15);
			} else if (mob instanceof Zombie) {
				checkRange(mob.HT, 70 + depth * 3, 70 + depth * 7, "感染僵尸生命");
				check(mob.defenseSkill == 9 + depth / 2 && mob.attackSkill(Dungeon.hero) == 15 + depth,
						"感染僵尸深度属性错误");
				checkRolls(mob, 10 + depth, 20 + depth, 3, 8);
			} else if (mob instanceof ManySkeleton) {
				check(mob.HT == 100 && mob.defenseSkill == 5
						&& mob.attackSkill(Dungeon.hero) == 30 + depth / 2,
						"巨型骷髅属性偏离旧版");
				checkRolls(mob, 4, 7, 0, 0);
			} else if (mob instanceof Orc) {
				check(mob.HT == 400 && mob.defenseSkill == 30 && mob.attackSkill(Dungeon.hero) == 35,
						"兽人属性偏离旧版");
				check(Math.abs(mob.attackDelay() - 1.5f) < 0.001f, "兽人攻击延迟错误");
				checkRolls(mob, 50, 90, 16, 32);
			} else if (mob instanceof GoldOrc) {
				check(mob.HT == 500 && mob.defenseSkill == 35 && mob.attackSkill(Dungeon.hero) == 55,
						"黄金兽人属性偏离旧版");
				check(Math.abs(mob.attackDelay() - 1.5f) < 0.001f, "黄金兽人攻击延迟错误");
				checkRolls(mob, 55, 115, 16, 32);
			} else if (mob instanceof Greatmoss) {
				checkRange(mob.HT, 120 + depth * 5, 120 + depth * 7, "巨型苔藓生命");
				check(mob.defenseSkill == depth && mob.attackSkill(Dungeon.hero) == 28 + depth / 2,
						"巨型苔藓深度属性错误");
				check(Math.abs(mob.attackDelay() - 2f) < 0.001f, "巨型苔藓攻击延迟错误");
				checkRolls(mob, 20 + depth, 60 + depth / 2, 20, 25);
			} else if (mob instanceof Brute) {
				checkRange(mob.HT, 120 + depth, 120 + depth * 2, "蛮兵生命");
				check(mob.defenseSkill == 10 + depth && mob.attackSkill(Dungeon.hero) == 10 + depth / 2
						&& mob.maxLvl == 25, "蛮兵深度属性错误");
				check(Math.abs(mob.attackDelay() - 1.5f) < 0.001f, "蛮兵攻击延迟错误");
				checkRolls(mob, 5 + depth, 25 + depth, 0, 10);
			} else if (mob instanceof FlyingProtector) {
				check(mob.HT == 50 + depth * 4 && mob.defenseSkill == 4 + depth
						&& mob.attackSkill(Dungeon.hero) == 9 + depth, "飞行守卫深度属性错误");
				checkRolls(mob, 20, 30, 0, depth);
			} else if (mob instanceof FireElemental) {
				checkRange(mob.HT, 120 + depth * 4, 120 + depth * 7, "火元素生命");
				check(mob.defenseSkill == 20 + depth && mob.attackSkill(Dungeon.hero) == 25 + depth / 2,
						"火元素深度属性错误");
				checkRolls(mob, 16, 20 + depth / 2, 0, 5);
			} else if (mob instanceof LevelChecker) {
				int heroLevel = Math.max(1, Dungeon.hero.lvl);
				check(mob.HT == 200 + Math.min(800, heroLevel * 20)
						&& mob.defenseSkill == heroLevel / 2 && mob.attackSkill(Dungeon.hero) == heroLevel,
						"等级裁判属性偏离旧版");
				checkRolls(mob, (int)(heroLevel * 1.5f), (int)(heroLevel * 1.5f), heroLevel, heroLevel);
			} else if (mob instanceof com.shatteredpixel.shatteredpixeldungeon.actors.mobs.PatrolUAV) {
				checkRange(mob.HT, 50 + depth, 50 + depth * 3, "巡逻无人机生命");
				check(mob.defenseSkill == depth / 2 && mob.attackSkill(Dungeon.hero) == 5,
						"巡逻无人机深度属性错误");
				checkRolls(mob, 4, 7, 5, 10);
			}
		}
	}

	private static void validateBruteEnrage() {
		Dungeon.depth = ChallengeJournal.anchorDepth(6);
		Dungeon.branch = ChallengeJournal.branchFor(6);
		Brute brute = new Brute();
		brute.pos = Dungeon.level.entrance;
		brute.HP = brute.HT / 4;
		brute.damage(1, SpsTriangleLevelsTest.class);
		check(brute.buff(DefenceUp.class) != null && brute.buff(DefenceUp.class).level() == 70,
				"蛮兵低血量时没有获得旧版三级回合70级防御强化");
		for (int i = 0; i < 24; i++) {
			checkRange(brute.damageRoll(), 57, 72, "狂暴蛮兵伤害");
			check(brute.drRoll() == 0, "狂暴蛮兵不应再有物理减伤");
		}
		brute.HP = 0;
		check(!brute.isAlive(), "SPS蛮兵死亡后不应触发破碎版护盾续命");
	}

	private static void validateTrialKeys(Level level, int expectedDepth) {
		int lockedChests = 0;
		int keys = 0;
		for (Heap heap : level.heaps.values()) {
			if (heap.type == Heap.Type.LOCKED_CHEST) lockedChests++;
			for (Item item : heap.items) {
				if (item instanceof GoldenKey) {
					keys += item.quantity();
					check(((GoldenKey)item).depth == expectedDepth,
							"试炼金钥匙深度错误：" + ((GoldenKey)item).depth + "，期望" + expectedDepth);
				}
			}
		}
		check(keys == lockedChests, "试炼上锁宝箱与金钥匙数量不匹配");
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

	private static void validateLegacyStandardThemes(SpsTriangleLevel courage,
			SpsTriangleLevel power, SpsTriangleLevel wisdom) {
		int expectedRemains = 0;
		int courageRooms = 0;
		for (Room room : courage.legacyLayout.rooms) {
			if (room.type != Type.STANDARD) continue;
			courageRooms++;
			expectedRemains += Math.max(room.width() - 1, room.height() - 1) / 2;
			for (int y = room.top + 1; y < room.bottom; y++) {
				for (int x = room.left + 1; x < room.right; x++) {
					check(courage.map[x + y * courage.width()] == Terrain.GRASS,
							"勇气试炼普通房没有全部绘制为旧版墓室");
				}
			}
		}
		check(courageRooms >= 4, "勇气试炼普通房少于四个");
		check(countHeapType(courage, Heap.Type.REMAINS) == expectedRemains,
				"勇气试炼墓室遗骸数量错误");

		int powerRooms = 0;
		for (Room room : power.legacyLayout.rooms) {
			if (room.type != Type.STANDARD) continue;
			powerRooms++;
			if (room.width() > room.height()) {
				for (int x = room.left + 2; x < room.right; x += 2) {
					for (int y = room.top + 1; y < room.bottom; y++) {
						check(power.map[x + y * power.width()] == Terrain.OLD_HIGH_GRASS,
								"力量试炼普通房缺少旧版横向古高草带");
					}
				}
			} else {
				for (int y = room.top + 2; y < room.bottom; y += 2) {
					for (int x = room.left + 1; x < room.right; x++) {
						check(power.map[x + y * power.width()] == Terrain.OLD_HIGH_GRASS,
								"力量试炼普通房缺少旧版纵向古高草带");
					}
				}
			}
		}
		check(powerRooms >= 4, "力量试炼普通房少于四个");

		int wisdomRooms = 0;
		for (Room room : wisdom.legacyLayout.rooms) {
			if (room.type != Type.STANDARD) continue;
			wisdomRooms++;
			int centerX = (room.left + room.right) / 2;
			int centerY = (room.top + room.bottom) / 2;
			int centerWidth = ((room.right - room.left) & 1) == 1 ? 2 : 1;
			int centerHeight = ((room.bottom - room.top) & 1) == 1 ? 2 : 1;
			int pedestals = 0;
			for (int y = centerY; y < centerY + centerHeight; y++) {
				for (int x = centerX; x < centerX + centerWidth; x++) {
					if (wisdom.map[x + y * wisdom.width()] == Terrain.PEDESTAL) pedestals++;
				}
			}
			check(pedestals == 1,
					"智慧试炼普通书房缺少中央基座");
		}
		check(wisdomRooms >= 4, "智慧试炼普通房少于四个");
	}

	private static <T extends SpsTriangleLevel> T build(T level, int challenge, int seed) {
		Dungeon.depth = ChallengeJournal.anchorDepth(challenge);
		Dungeon.branch = ChallengeJournal.branchFor(challenge);
		Dungeon.triforceOfCourage = false;
		Dungeon.triforceOfPower = false;
		Dungeon.triforceOfWisdom = false;
		Random.pushGenerator(0x535053545249414EL + 1000L * challenge + seed);
		try {
			level.prepareLegacyTrial();
			check(countQueued(level, Food.class) == 2,
					level.getClass().getSimpleName() + "必须预排两份旧版食物");
			check(countQueued(level, ScrollOfUpgrade.class) == 1,
					level.getClass().getSimpleName() + "必须预排一张升级卷轴");
			check(countQueued(level, Stylus.class) == countQueued(level, Weightstone.class),
					level.getClass().getSimpleName() + "刻印笔与磨刀石没有成对预排");
			check(challenge != 5 || level.feeling == Level.Feeling.DARK && level.viewDistance == 3,
					"勇气试炼没有恢复旧版黑暗视野");
			check(challenge != 7 || level.feeling == Level.Feeling.TRAP,
					"智慧试炼没有恢复旧版陷阱层感");
			level.transitions = new ArrayList<>();
			level.customTiles = new ArrayList<>();
			level.customTerrain = new ArrayList<>();
			level.customWalls = new ArrayList<>();
			level.mobs = new HashSet<>();
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
			check(level.transitions.size() == 1, level.getClass().getSimpleName() + "必须只有一个返回过渡");
			LevelTransition transition = level.transitions.get(0);
			check(transition.cell() == level.entrance && transition.type == LevelTransition.Type.BRANCH_ENTRANCE
					&& transition.destDepth == Dungeon.depth && transition.destBranch == 0
					&& transition.destType == LevelTransition.Type.REGULAR_ENTRANCE,
					level.getClass().getSimpleName() + "返回过渡目标错误");
			check(level.map[level.entrance] == level.entranceTerrain(),
					level.getClass().getSimpleName() + "入口地形错误");
			check(level.map[level.exit] == Terrain.PEDESTAL,
					level.getClass().getSimpleName() + "终点必须为三角基座");
			check(pathExists(level, level.entrance, level.exit),
					level.getClass().getSimpleName() + "入口到三角奖励不可达");
			return level;
		} finally {
			Random.popGenerator();
		}
	}

	private static int countQueued(SpsTriangleLevel level, Class<? extends Item> type) {
		int result = 0;
		for (Item item : level.itemsToSpawn) if (type.isInstance(item)) result += item.quantity();
		return result;
	}

	private static boolean pathExists(Level level, int start, int goal) {
		boolean[] seen = new boolean[level.length()];
		ArrayList<Integer> pending = new ArrayList<>();
		pending.add(start);
		while (!pending.isEmpty()) {
			int cell = pending.remove(pending.size() - 1);
			if (cell == goal) return true;
			if (cell < 0 || cell >= level.length() || seen[cell] || !traversable(level, cell)) continue;
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

	private static boolean traversable(Level level, int cell) {
		int terrain = level.map[cell];
		return (Terrain.flags[terrain] & Terrain.SOLID) == 0
				|| terrain == Terrain.DOOR || terrain == Terrain.SECRET_DOOR
				|| terrain == Terrain.LOCKED_DOOR || terrain == Terrain.BARRICADE
				|| terrain == Terrain.BOOKSHELF;
	}

	private static void checkReward(Level level, Class<? extends Item> reward) {
		Heap heap = level.heaps.get(level.exit);
		check(heap != null && heap.items.size() == 1 && reward.isInstance(heap.peek()),
				level.getClass().getSimpleName() + "终点三角奖励错误");
	}

	@SafeVarargs
	private static void checkMobPool(Level level, int min, int max, Set<Class<?>> seen,
			Class<? extends Mob>... allowed) {
		int matchedCount = 0;
		for (Mob mob : level.mobs) {
			boolean matched = false;
			for (Class<? extends Mob> type : allowed) {
				if (type.isInstance(mob)) {
					seen.add(type);
					matchedCount++;
					matched = true;
					break;
				}
			}
			check(matched || mob instanceof Mimic || mob instanceof Sentinel
					|| mob instanceof com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye,
					level.getClass().getSimpleName() + "生成了错误敌人：" + mob.getClass().getName());
		}
		check(matchedCount >= min && matchedCount <= max,
				level.getClass().getSimpleName() + "敌人数量错误，实际=" + matchedCount);
	}

	private static int countItems(Level level, Class<? extends Item> type) {
		int result = 0;
		for (Heap heap : level.heaps.values()) for (Item item : heap.items) {
			if (type.isInstance(item)) result += item.quantity();
		}
		return result;
	}

	private static int countHeapType(Level level, Heap.Type type) {
		int result = 0;
		for (Heap heap : level.heaps.values()) if (heap.type == type) result++;
		return result;
	}

	private static int countTerrain(Level level, int terrain) {
		int result = 0;
		for (int value : level.map) if (value == terrain) result++;
		return result;
	}

	private static int countMobs(Level level, Class<? extends Mob> type) {
		int result = 0;
		for (Mob mob : level.mobs) if (type.isInstance(mob)) result++;
		return result;
	}

	private static int countPlants(Level level, Class<? extends Plant> type) {
		int result = 0;
		for (Plant plant : level.plants.values()) if (type.isInstance(plant)) result++;
		return result;
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

	private static final class RecordingCourageLevel extends TriangleCLevel {
		@Override public Heap drop(Item item, int cell) { return recordDrop(this, item, cell); }
	}

	private static final class RecordingPowerLevel extends TrianglePLevel {
		@Override public Heap drop(Item item, int cell) { return recordDrop(this, item, cell); }
	}

	private static final class RecordingWisdomLevel extends TriangleWLevel {
		@Override public Heap drop(Item item, int cell) { return recordDrop(this, item, cell); }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
