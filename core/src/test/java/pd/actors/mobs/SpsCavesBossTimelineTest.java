package pd.actors.mobs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Challenges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.DarkGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.hero.Hero;
import pd.effects.Pushing;
import pd.items.Heap;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Runtime parity checks for all three SPS-PD 0.9.8 caves bosses. */
public final class SpsCavesBossTimelineTest {

	private static final int WIDTH = 48;
	private static final int CENTER = 24 + 24 * WIDTH;

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-caves-boss-timeline" + File.separator);
		Game.version = "test";
		Random.pushGenerator(0x5350534341564553L);
		try {
			testDmTowerPower();
			testHybridFinalPhase();
			testSpiderFamily();
			System.out.println("SPS洞穴首领时序通过：DM-300塔能、混源体分裂及蜘蛛女王产卵/孵化/深度成长均符合0.9.8。");
		} finally {
			Random.popGenerator();
			Dungeon.challenges = 0;
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testDmTowerPower() {
		RecordingLevel level = setup();
		TestDm300 dm = add(level, new TestDm300(), CENTER);
		dm.state = dm.PASSIVE;
		for (int i = 0; i < 20; i++) {
			check(dm.damageRoll() == 0 && dm.drRoll() == 10,
					"DM-300生成塔前错误获得塔能加成");
		}
		check(dm.takeTurn(), "DM-300首次行动未完成");
		check(count(level, SpsDM300.Tower.class) == 2, "DM-300首次行动没有生成两座塔");
		assertDmPowered(dm);

		for (Mob mob : new ArrayList<>(level.mobs)) {
			if (mob instanceof SpsDM300.Tower) {
				mob.HP = 0;
				level.mobs.remove(mob);
			}
		}
		check(count(level, SpsDM300.Tower.class) == 0, "测试未能移除DM-300塔");
		assertDmPowered(dm);

		Bundle bundle = new Bundle();
		dm.storeInBundle(bundle);
		SpsDM300 restored = new SpsDM300();
		restored.restoreFromBundle(bundle);
		assertDmPowered(restored);
	}

	private static void assertDmPowered(SpsDM300 dm) {
		for (int i = 0; i < 200; i++) {
			int damage = dm.damageRoll();
			int armor = dm.drRoll();
			check(damage >= 15 && damage <= 19,
					"DM-300塔能攻击倍率错误：" + damage);
			check(armor >= 10 && armor <= 14,
					"DM-300塔能减伤倍率错误：" + armor);
		}
	}

	private static void testHybridFinalPhase() throws Exception {
		RecordingLevel level = setup();
		Hybrid hybrid = add(level, new Hybrid(), CENTER);
		check(hybrid.isImmune(DarkGas.class), "混源体缺少旧版暗气免疫");
		Buff.affect(hybrid, Cripple.class, 2f);
		check(hybrid.speed() == 0.75f, "混源体基础速度绕过了旧版减速规则");
		hybrid.buff(Cripple.class).detach();

		Dungeon.challenges = Challenges.TEST_TIME;
		for (int i = 0; i < 100; i++) {
			int damage = hybrid.damageRoll();
			check(damage >= 0 && damage <= 1, "测试模式没有压低混源体伤害");
		}
		Dungeon.challenges = 0;

		Arrays.fill(level.map, Terrain.WALL);
		level.map[CENTER] = Terrain.EMPTY;
		level.map[CENTER + 1] = Terrain.DOOR;
		level.rebuildFlags();
		setInt(hybrid, "breaks", 3);
		hybrid.damage(10, SpsCavesBossTimelineTest.class);
		Hybrid.Mixers clone = firstExact(level, Hybrid.Mixers.class);
		check(clone != null && clone.pos == CENTER + 1 && clone.HP == 10,
				"混源体第三阶段没有按来袭伤害生成分裂体");
		check(level.map[CENTER + 1] == Terrain.OPEN_DOOR,
				"混源体分裂体没有打开出生格的门");
		check(hasNegativePushing(), "混源体分裂缺少旧版负时间推出时序");
	}

	private static void testSpiderFamily() throws Exception {
		RecordingLevel level = setup();
		SpiderQueen.SpiderWorker worker = new SpiderQueen.SpiderWorker();
		SpiderQueen.SpiderMind mind = new SpiderQueen.SpiderMind();
		SpiderQueen.SpiderJumper jumper = new SpiderQueen.SpiderJumper();
		SpiderQueen.SpiderGold gold = new SpiderQueen.SpiderGold();
		check(worker.attackSkill(null) == 35 && mind.attackSkill(null) == 35
				&& jumper.attackSkill(null) == 35 && gold.attackSkill(null) == 55,
				"蜘蛛幼体没有使用洞穴15层的旧版命中成长");
		assertDepthDamage(worker, 12, 41, 26, "工蛛");
		assertDepthDamage(mind, 5, 25, 10, "心智蜘蛛");
		assertDepthDamage(jumper, 12, 41, 26, "跳蛛");
		assertDepthDamage(gold, 20, 45, 30, "金蛛");
		Buff.affect(gold, Cripple.class, 2f);
		check(gold.speed() == 0.375f, "金蛛基础速度绕过了旧版减速规则");

		Arrays.fill(level.map, Terrain.WALL);
		level.map[CENTER] = Terrain.INACTIVE_TRAP;
		level.map[CENTER + 1] = Terrain.DOOR;
		level.rebuildFlags();
		SpiderQueen queen = add(level, new SpiderQueen(), CENTER - 1);
		queen.move(CENTER, false);
		SpiderQueen.SpiderEgg egg = firstExact(level, SpiderQueen.SpiderEgg.class);
		check(egg != null && egg.pos == CENTER + 1 && queen.HP == queen.HT - 1,
				"蜘蛛女王踩充能格后没有产卵并支付1点生命");
		check(level.map[CENTER + 1] == Terrain.OPEN_DOOR,
				"蜘蛛卵没有打开出生格的门");
		check(hasNegativePushing(), "蜘蛛女王产卵缺少旧版负时间推出时序");

		testHatch(0, SpiderQueen.SpiderWorker.class);
		testHatch(6, SpiderQueen.SpiderMind.class);
		testHatch(11, SpiderQueen.SpiderJumper.class);
		testHatch(21, SpiderQueen.SpiderGold.class);

		level = setup();
		SpiderQueen.SpiderEgg saved = add(level, new SpiderQueen.SpiderEgg(), CENTER);
		setInt(saved, "life", 11);
		Bundle bundle = new Bundle();
		saved.storeInBundle(bundle);
		SpiderQueen.SpiderEgg restored = new SpiderQueen.SpiderEgg();
		restored.restoreFromBundle(bundle);
		check(getInt(restored, "life") == 11, "蜘蛛卵孵化时间没有随存档恢复");

		level = setup();
		TestSpiderMind healer = add(level, new TestSpiderMind(), CENTER);
		Target target = add(level, new Target(), CENTER + 1);
		healer.HP = healer.HT - 1;
		for (int i = 0; i < 20 && healer.HP <= healer.HT; i++) healer.strike(target, 100);
		check(healer.HP > healer.HT, "心智蜘蛛错误地丢失旧版越上限治疗");
	}

	private static void assertDepthDamage(Mob mob, int min, int max, int oldMax, String name) {
		int seenMax = Integer.MIN_VALUE;
		for (int i = 0; i < 400; i++) {
			int damage = mob.damageRoll();
			check(damage >= min && damage <= max, name + "伤害越界：" + damage);
			seenMax = Math.max(seenMax, damage);
		}
		check(seenMax > oldMax, name + "未实际获得旧版深度伤害成长");
	}

	private static void testHatch(int life, Class<? extends Mob> expected) throws Exception {
		RecordingLevel level = setup();
		SpiderQueen.SpiderEgg egg = add(level, new SpiderQueen.SpiderEgg(), CENTER);
		setInt(egg, "life", life);
		egg.die(SpsCavesBossTimelineTest.class);
		check(firstExact(level, expected) != null,
				"蜘蛛卵在寿命" + life + "时孵化类型错误，应为" + expected.getSimpleName());
	}

	private static RecordingLevel setup() {
		Actor.clear();
		Dungeon.depth = 15;
		Dungeon.branch = 0;
		Dungeon.challenges = 0;
		Dungeon.quickslot = new QuickSlot();
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.pos = CENTER - WIDTH;
		hero.HP = hero.HT = 1000;
		hero.fieldOfView = new boolean[level.length()];
		Dungeon.hero = hero;
		Actor.add(hero);
		return level;
	}

	private static <T extends Mob> T add(RecordingLevel level, T mob, int pos) {
		mob.pos = pos;
		level.mobs.add(mob);
		Actor.add(mob);
		return mob;
	}

	private static int count(RecordingLevel level, Class<? extends Mob> type) {
		int result = 0;
		for (Mob mob : level.mobs) if (type.isInstance(mob) && mob.isAlive()) result++;
		return result;
	}

	private static <T extends Mob> T firstExact(RecordingLevel level, Class<T> type) {
		for (Mob mob : level.mobs) if (mob.getClass() == type && mob.isAlive()) return type.cast(mob);
		return null;
	}

	private static boolean hasNegativePushing() {
		for (Actor actor : Actor.all()) {
			if (actor instanceof Pushing && actor.cooldown() < 0f) return true;
		}
		return false;
	}

	private static void setInt(Object object, String name, int value) throws Exception {
		Field field = object.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.setInt(object, value);
	}

	private static int getInt(Object object, String name) throws Exception {
		Field field = object.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return field.getInt(object);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestDm300 extends SpsDM300 {
		boolean takeTurn() { return act(); }
	}

	private static final class TestSpiderMind extends SpiderQueen.SpiderMind {
		int strike(Char enemy, int damage) { return attackProc(enemy, damage); }
	}

	private static final class Target extends Mob {
		Target() { HP = HT = 1000; defenseSkill = 0; }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }
	}

	private static final class RecordingLevel extends Level {
		RecordingLevel() {
			setSize(WIDTH, WIDTH);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<Trap>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
			heroFOV = new boolean[length()];
		}

		void rebuildFlags() {
			buildFlagMaps();
			heroFOV = new boolean[length()];
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private SpsCavesBossTimelineTest() { }
}
