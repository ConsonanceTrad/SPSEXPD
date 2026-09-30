package pd.actors.mobs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.Badges;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSprite;
import pd.sprites.CharSprite;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsMainBossTimelineTest {

	private static final int CENTER = 24 + 24 * 48;

	public static void main(String[] args) {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-main-boss-timelines" + File.separator);
		Game.version = "test";
		Badges.loadGlobal();
		Random.pushGenerator(0x535053424F535354L);
		try {
			testSewerHeartBeam();
			testBrokenRobotBeam();
			testKingTombPhase();
			testYogFistCycle();
			System.out.println("SPS主线首领时序测试通过：下水道之心光束、DM-300损坏机器人远射、国王墓碑第二阶段及Yog四拳循环与解锁均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testSewerHeartBeam() {
		RecordingLevel level = setup(5);
		TestSewerHeart heart = add(level, new TestSewerHeart(), CENTER);
		Target target = add(level, new Target(), CENTER + 5);
		heart.fieldOfView = visible(level);
		check(heart.canFire(target), "下水道之心无法锁定直线目标");
		check(heart.fire(target) && heart.beamCharged(), "下水道之心没有完成第一回合蓄力");
		check(heart.canFire(target), "下水道之心蓄力后丢失旧目标");
		int hp = target.HP;
		check(heart.fire(target) && !heart.beamCharged(), "无精灵时下水道之心光束没有同步结算");
		check(target.HP < hp, "下水道之心光束没有造成伤害");
	}

	private static void testBrokenRobotBeam() {
		RecordingLevel level = setup(15);
		TestBrokenRobot robot = add(level, new TestBrokenRobot(), CENTER);
		Target target = add(level, new Target(), CENTER + 6);
		check(robot.canFire(target), "损坏机器人无法锁定直线目标");
		int hp = target.HP;
		check(robot.fire(target), "无精灵时损坏机器人远射没有同步结束回合");
		check(target.HP < hp, "损坏机器人远射没有造成伤害");
	}

	private static void testKingTombPhase() {
		RecordingLevel level = setup(20);
		level.seal();
		TestKing king = add(level, new TestKing(), CENTER);
		king.ensureTomb();
		King.DwarfKingTomb tomb = first(level, King.DwarfKingTomb.class);
		check(tomb != null, "矮人国王没有生成永恒之墓");
		int tombHp = tomb.HP;
		tomb.damage(100, Dungeon.hero);
		check(tomb.HP == tombHp, "国王存活时永恒之墓仍可受伤");
		attachDeathSprite(king);
		king.die(Dungeon.hero);
		check(count(level, DwarfLich.class) == 4, "国王死亡后没有在墓碑四周生成四名巫妖");
		tomb.damage(100, Dungeon.hero);
		check(tomb.HP < tombHp, "国王死亡后永恒之墓仍然无敌");
		prepareDeaths(level);
		int heroHp = Dungeon.hero.HP;
		Dungeon.hero.HP = 0;
		tomb.die(Dungeon.hero);
		Dungeon.hero.HP = heroHp;
		check(!level.locked, "永恒之墓死亡后没有解锁矮人城出口");
		check(count(level, DwarfLich.class) == 0, "永恒之墓死亡后巫妖没有清空");
		check(level.itemCount() >= 6, "永恒之墓第二阶段奖励不完整");
	}

	private static void testYogFistCycle() {
		RecordingLevel level = setup(25);
		level.seal();
		TestYog yog = add(level, new TestYog(), CENTER);
		yog.spawnFists();
		check(countOwnedFists(level, yog) == 4 && yog.drRoll() == 120,
				"Yog初始四拳或每拳30点减伤错误");
		yog.HP = 100;
		yog.damage(200, Dungeon.hero);
		check(yog.HP == 99 && yog.isAlive(), "Yog没有把英雄造成的过量伤害按旧版压为1点");
		removeOwnedFists(level, yog);
		int hp = yog.HP = 500;
		yog.defend(Dungeon.hero, 1);
		check(countOwnedFists(level, yog) == 4 && yog.HP == hp - 50,
				"四拳清空后Yog没有重召并支付50点生命");
		check(countOwnedLarvae(level, yog) == 1, "Yog受击时没有生成幼虫");
		removeOwnedFists(level, yog);
		yog.HP = 100;
		prepareDeaths(level);
		int heroHp = Dungeon.hero.HP;
		Dungeon.hero.HP = 0;
		yog.damage(100, Dungeon.hero);
		Dungeon.hero.HP = heroHp;
		check(!yog.isAlive() && !level.locked, "四拳清空后Yog死亡没有解锁出口");
		check(countOwnedLarvae(level, yog) == 0, "Yog死亡后幼虫没有清空");
		check(level.itemCount() >= 2, "Yog死亡奖励不完整");
	}

	private static RecordingLevel setup(int depth) {
		Actor.clear();
		Dungeon.depth = depth;
		Dungeon.branch = 0;
		Dungeon.quickslot = new QuickSlot();
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.pos = CENTER - 48;
		hero.lvl = Hero.MAX_LEVEL;
		hero.fieldOfView = visible(level);
		hero.sprite = new CharSprite() {
			@Override public void showStatusWithIcon(int color, String text, int icon, Object... args) { }
		};
		Dungeon.hero = hero;
		Actor.add(hero);
		return level;
	}

	private static boolean[] visible(Level level) {
		boolean[] result = new boolean[level.length()];
		Arrays.fill(result, true);
		return result;
	}

	private static <T extends Mob> T add(RecordingLevel level, T mob, int pos) {
		mob.pos = pos;
		level.mobs.add(mob);
		Actor.add(mob);
		return mob;
	}

	private static <T extends Mob> T first(RecordingLevel level, Class<T> type) {
		for (Mob mob : level.mobs) if (type.isInstance(mob) && mob.isAlive()) return type.cast(mob);
		return null;
	}

	private static int count(RecordingLevel level, Class<? extends Mob> type) {
		int count = 0;
		for (Mob mob : level.mobs) if (type.isInstance(mob) && mob.isAlive()) count++;
		return count;
	}

	private static int countOwnedFists(RecordingLevel level, SpsYog yog) {
		int count = 0;
		for (Mob mob : level.mobs) if (mob instanceof SpsYog.Fist
				&& ((SpsYog.Fist) mob).ownerId == yog.id() && mob.isAlive()) count++;
		return count;
	}

	private static int countOwnedLarvae(RecordingLevel level, SpsYog yog) {
		int count = 0;
		for (Mob mob : level.mobs) if (mob instanceof SpsYog.Larva
				&& ((SpsYog.Larva) mob).ownerId == yog.id() && mob.isAlive()) count++;
		return count;
	}

	private static void removeOwnedFists(RecordingLevel level, SpsYog yog) {
		for (Mob mob : new ArrayList<>(level.mobs)) if (mob instanceof SpsYog.Fist
				&& ((SpsYog.Fist) mob).ownerId == yog.id()) {
			attachDeathSprite(mob);
			mob.die(SpsMainBossTimelineTest.class);
		}
	}

	private static void prepareDeaths(RecordingLevel level) {
		for (Mob mob : level.mobs) attachDeathSprite(mob);
	}

	private static void attachDeathSprite(Mob mob) {
		if (mob.sprite == null) mob.sprite = new CharSprite() {
			@Override public void die() { }
		};
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestSewerHeart extends SewerHeart {
		boolean canFire(Char target) { return canAttack(target); }
		boolean fire(Char target) { return doAttack(target); }
	}

	private static final class TestBrokenRobot extends SpsDM300.BrokenRobot {
		boolean canFire(Char target) { return canAttack(target); }
		boolean fire(Char target) { return doAttack(target); }
	}

	private static final class TestKing extends King {
		void ensureTomb() {
			try {
				java.lang.reflect.Method method = King.class.getDeclaredMethod("spawnTomb");
				method.setAccessible(true);
				method.invoke(this);
			} catch (ReflectiveOperationException error) {
				throw new AssertionError("无法触发国王墓碑阶段", error);
			}
		}
	}

	private static final class TestYog extends Yog {
		int defend(Char enemy, int damage) { return defenseProc(enemy, damage); }
	}

	private static final class Target extends Mob {
		Target() { HP = HT = 1000; defenseSkill = 0; }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }
	}

	private static final class RecordingLevel extends Level {
		RecordingLevel() {
			setSize(48, 48);
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
			heroFOV = visible(this);
		}
		int itemCount() {
			int count = 0;
			for (Heap heap : heaps.valueList()) count += heap.items.size();
			return count;
		}
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heap.sprite = new ItemSprite() {
					@Override public void place(int cell) { }
					@Override public void drop() { }
					@Override public void drop(int from) { }
				};
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private SpsMainBossTimelineTest() { }
}
