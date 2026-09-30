package pd.items.quest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Poison;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import watabou.noosa.Game;
import watabou.utils.PathFinder;
import watabou.utils.Random;
import watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsPickaxeTest {

	private static final int CENTER = 8 + 8 * 16;

	public static void main(String[] args) {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testStatsAndDepths();
			testMining();
			testCombatEffects();
			System.out.println("SPS镐子测试通过：旧版数值、有效深度、八邻格暗金采掘、地形变化和三种战斗异常均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testStatsAndDepths() {
		Pickaxe pickaxe = new Pickaxe();
		check(pickaxe.min(0) == 10 && pickaxe.max(0) == 22 && pickaxe.STRReq(0) == 14,
				"镐子0级数值未恢复0.9.8");
		check(pickaxe.min(3) == 19 && pickaxe.max(3) == 31 && pickaxe.STRReq(3) == 14,
				"镐子升级成长未恢复每级双端+3");
		check(Pickaxe.legacyMiningDepth(11) && Pickaxe.legacyMiningDepth(15)
				&& Pickaxe.legacyMiningDepth(32), "镐子允许采掘的旧版楼层不完整");
		check(!Pickaxe.legacyMiningDepth(10) && !Pickaxe.legacyMiningDepth(16)
				&& !Pickaxe.legacyMiningDepth(31), "镐子在旧版禁用楼层仍可采掘");
	}

	private static void testMining() {
		TestLevel level = freshLevel(11);
		Hero hero = freshHero();
		int target = hero.pos + PathFinder.NEIGHBOURS8[0];
		Level.set(target, Terrain.WALL_DECO, level);
		Pickaxe pickaxe = new Pickaxe();
		check(pickaxe.actions(hero).contains(Pickaxe.AC_MINE), "镐子缺少采掘动作");
		check(Pickaxe.AC_MINE.equals(pickaxe.defaultAction()), "镐子默认动作不是采掘");
		check(pickaxe.mine(hero), "镐子无法在11层采掘暗金矿脉");
		check(level.map[target] == Terrain.WALL, "暗金矿脉采掘后没有恢复为普通墙体");
		DarkGold gold = hero.belongings.getItem(DarkGold.class);
		check(gold != null && gold.quantity() == 1, "采掘没有获得一块暗金");

		level = freshLevel(16);
		hero = freshHero();
		target = hero.pos + PathFinder.NEIGHBOURS8[0];
		Level.set(target, Terrain.WALL_DECO, level);
		check(!pickaxe.mine(hero) && level.map[target] == Terrain.WALL_DECO,
				"镐子在16层错误采掘了暗金矿脉");
	}

	private static void testCombatEffects() {
		freshLevel(12);
		Hero hero = freshHero();
		TestMob defender = new TestMob();
		defender.HP = defender.HT = 10000;
		defender.pos = CENTER + 1;
		Dungeon.level.mobs.add(defender);
		Actor.add(defender);
		Random.pushGenerator(0x5049434B415845L);
		try {
			Pickaxe pickaxe = new Pickaxe();
			for (int i = 0; i < 200; i++) pickaxe.proc(hero, defender, 10);
		} finally {
			Random.popGenerator();
		}
		check(defender.buff(Bleeding.class) != null, "镐子没有恢复流血触发");
		check(defender.buff(Ooze.class) != null, "镐子没有恢复酸蚀触发");
		check(defender.buff(Poison.class) != null, "镐子没有恢复中毒触发");
	}

	private static TestLevel freshLevel(int depth) {
		Actor.clear();
		Dungeon.depth = depth;
		Dungeon.branch = 0;
		Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		return level;
	}

	private static Hero freshHero() {
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.pos = CENTER;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestMob extends Mob {
		@Override public int drRoll() { return 0; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
			Arrays.fill(heroFOV, true);
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }

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

	private SpsPickaxeTest() { }
}
