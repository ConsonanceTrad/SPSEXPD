package pd.items.bombs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.actors.mobs.Hybrid;
import pd.actors.mobs.Mob;
import pd.items.Heap;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/** Runtime checks for Hybrid's SPS-PD 0.9.8 phase-change bomb. */
public final class SpsDangerousBombTest {

	private static final int WIDTH = 16;
	private static final int CENTER = 8 + 8 * WIDTH;

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-dangerous-bomb" + File.separator);
		Game.version = "test";
		Random.pushGenerator(0x53505344414E4745L);
		try {
			testBlastBehavior();
			testBoundarySafety();
			testHybridPhaseTrigger();
			System.out.println("SPS危险炸弹测试通过：普通爆炸、英雄追加伤害、两格地形破坏、边界保护和混源体阶段触发均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testBlastBehavior() {
		TestLevel level = freshLevel();
		TestHero hero = heroAt(CENTER + 2, 1000);
		TestMob nearMob = mobAt(level, CENTER + 1, 1000);
		TestMob farMob = mobAt(level, CENTER - 2 * WIDTH, 1000);
		level.map[CENTER - 1 + WIDTH] = Terrain.WALL;
		level.map[CENTER - 1 - WIDTH] = Terrain.GLASS_WALL;
		level.map[CENTER + 2 * WIDTH] = Terrain.HIGH_GRASS;
		level.buildFlagMaps();
		Arrays.fill(level.heroFOV, false);

		DangerousBomb bomb = new DangerousBomb();
		check(bomb.image == ItemSpriteSheet.HUGE_BOMB && bomb.value() == 20,
				"危险炸弹图标或售价错误");
		bomb.explode(CENTER);

		check(nearMob.HP < nearMob.HT, "危险炸弹没有保留普通炸弹的近距离伤害");
		check(farMob.HP == farMob.HT, "危险炸弹错误地对两格外普通单位施加比例伤害");
		check(hero.HP <= 875 && hero.HP >= 750, "危险炸弹没有对两格外英雄造成八分之一至四分之一生命伤害");
		check(level.map[CENTER - 1 + WIDTH] == Terrain.EMPTY, "危险炸弹没有摧毁普通墙");
		check(level.map[CENTER - 1 - WIDTH] == Terrain.EMPTY, "危险炸弹没有摧毁玻璃墙");
		check(level.map[CENTER + 2 * WIDTH] == Terrain.EMBERS, "危险炸弹没有烧毁可燃地形");
	}

	private static void testBoundarySafety() {
		TestLevel level = freshLevel();
		TestHero hero = heroAt(WIDTH + 2, 1000);
		DangerousBomb bomb = new DangerousBomb();
		boolean[] affected = bomb.secondaryBlastCells(WIDTH + 1);
		check(affected.length == level.length() && affected[WIDTH + 1], "危险炸弹边界范围计算错误");
		bomb.explode(WIDTH + 1);
		check(hero.HP < hero.HT, "边界附近的英雄没有受到危险炸弹追加伤害");
	}

	private static void testHybridPhaseTrigger() throws Exception {
		TestLevel level = freshLevel();
		TestHero hero = heroAt(CENTER + 2, 1000);
		Hybrid hybrid = new Hybrid();
		hybrid.pos = CENTER;
		hybrid.HP = hybrid.HT / 2;
		level.mobs.add(hybrid);
		Actor.add(hybrid);
		Method act = Hybrid.class.getDeclaredMethod("act");
		act.setAccessible(true);
		check(Boolean.TRUE.equals(act.invoke(hybrid)), "混源体阶段爆炸没有完成回合");
		check(hybrid.buff(ShieldArmor.class) != null
				&& hybrid.buff(ShieldArmor.class).level() == 200, "混源体阶段爆炸后没有获得200层物理护盾");
		check(hero.HP < hero.HT, "混源体阶段触发没有使用危险炸弹伤害英雄");
	}

	private static TestLevel freshLevel() {
		Actor.clear();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		return level;
	}

	private static TestHero heroAt(int pos, int health) {
		TestHero hero = new TestHero();
		hero.pos = pos;
		hero.HP = hero.HT = health;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static TestMob mobAt(TestLevel level, int pos, int health) {
		TestMob mob = new TestMob();
		mob.pos = pos;
		mob.HP = mob.HT = health;
		level.mobs.add(mob);
		Actor.add(mob);
		return mob;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestHero extends Hero {
		@Override public int drRoll() { return 0; }
		@Override public void damage(int damage, Object source) { HP = Math.max(0, HP - Math.max(0, damage)); }
	}

	private static final class TestMob extends Mob {
		@Override public int drRoll() { return 0; }
		@Override public void damage(int damage, Object source) { HP = Math.max(0, HP - Math.max(0, damage)); }
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
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
			Arrays.fill(heroFOV, false);
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
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

	private SpsDangerousBombTest() { }
}
