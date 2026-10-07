package pd.items.equipment.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StoneOre;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/** Headless checks for the void hand: gold cost by level, explored-only pulls and growth. */
public final class SpsVoidHandTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x535053564F49445FL);
		try {
			testStatsAndActions();
			testPullAndGrowth();
			testPoolAndMessages();
			System.out.println("SPS虚空之手测试通过：费用与等级减免、已探索判定、金币不足拒绝、取回与成长、神器池与双语文本均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testStatsAndActions() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		VoidHand hand = new VoidHand();
		check(hand.levelCap() == 10 && hand.level() == 0 && hand.cost() == 60,
				"虚空之手初始等级上限或费用错误");
		check(hand.image == pd.atlas.items.SpecificPlaceHolderDict.ARTIFACT_HOLDER_0,
				"虚空之手占位图标错误");
		check(!hand.actions(hero).contains(VoidHand.AC_PULL), "未装备时错误开放收取动作");

		hero.belongings.backpack.items.add(hand);
		check(hand.doEquip(hero), "虚空之手无法装备");
		check(hand.actions(hero).contains(VoidHand.AC_PULL), "装备后没有开放收取动作");

		hand.level(4);
		check(hand.cost() == 40, "四级虚空之手费用不是 40：" + hand.cost());
		hand.level(10);
		check(hand.cost() == VoidHand.MIN_COST, "十级虚空之手费用没有降到下限：" + hand.cost());
	}

	private static void testPullAndGrowth() {
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.pos = 27;
		Dungeon.hero = hero;
		Arrays.fill(level.map, Terrain.EMPTY);
		Arrays.fill(level.passable, true);
		Arrays.fill(level.heroFOV, true);
		level.buildFlagMaps();

		VoidHand hand = new VoidHand();
		hero.belongings.backpack.items.add(hand);
		check(hand.doEquip(hero), "虚空之手无法装备");

		//已探索格子上的掉落物
		int lootCell = 30;
		level.mapped[lootCell] = true;
		level.drop(new StoneOre(), lootCell);
		//未探索格子上的掉落物
		int hiddenCell = 40;
		level.drop(new Gold(50), hiddenCell);

		Dungeon.gold = 59;
		check(!hand.pullFrom(hero, lootCell), "金币不足时虚空之手仍能取走物品");
		check(level.heaps.get(lootCell) != null, "拒绝后掉落物不应被取走");

		Dungeon.gold = 200;
		check(hand.pullFrom(hero, lootCell), "虚空之手无法取回已探索区域的掉落物");
		check(level.heaps.get(lootCell) == null, "取回后原地仍保留掉落物");
		check(Dungeon.gold == 140, "取回没有按费用扣除金币：" + Dungeon.gold);
		check(hand.level() == 1 && hand.exp() == 0, "取回后没有按门槛升级：" + hand.level());
		check(hand.cost() == 55, "升级后费用没有减免 5：" + hand.cost());

		check(!hand.pullFrom(hero, hiddenCell), "虚空之手错误地取走了未探索区域的东西");
		check(level.heaps.get(hiddenCell) != null, "未探索区域的掉落物被错误取走");
		check(Dungeon.gold == 140, "失败的收取不应扣金币：" + Dungeon.gold);

		//空目标格
		check(!hand.pullFrom(hero, lootCell), "空目标格不应能收取");
	}

	private static void testPoolAndMessages() throws Exception {
		boolean inPool = false;
		for (Class<?> type : Generator.Category.ARTIFACT.classes) {
			if (type == VoidHand.class) inPool = true;
		}
		check(inPool, "虚空之手没有进入普通神器池");
		check(Generator.Category.ARTIFACT.classes.length == Generator.Category.ARTIFACT.defaultProbs.length,
				"神器池与权重数组长度不一致");

		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			String text = read(file);
			check(text.contains("items.equipment.artifacts.voidhand.desc="), file + "缺少虚空之手描述");
			check(text.contains("items.equipment.artifacts.voidhand.ac_pull="), file + "缺少收取动作名");
		}
	}

	private static String read(String path) throws Exception {
		return new String(Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			heroFOV = new boolean[length()];
			visited = new boolean[length()];
			mapped = new boolean[length()];
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsVoidHandTest() { }
}
