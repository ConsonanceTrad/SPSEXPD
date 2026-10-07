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

/** Headless checks for nature's axe: radius by level, harvesting grass and growth by harvest/trample. */
public final class SpsNaturalAxeTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534E41545845L);
		try {
			testStatsAndActions();
			testHarvestAndGrowth();
			testPoolAndMessages();
			System.out.println("SPS自然之斧测试通过：等级半径、收获高草、成长升级、神器池与双语文本均正常。");
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
		NaturalAxe axe = new NaturalAxe();
		check(axe.levelCap() == 10 && axe.level() == 0 && axe.radius() == 1,
				"自然之斧初始等级上限或收获半径错误");
		check(axe.image == pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict.HAND_AXE_0,
				"自然之斧占位图标错误");
		check(!axe.actions(hero).contains(NaturalAxe.AC_HARVEST), "未装备时错误开放收获动作");

		hero.belongings.backpack.items.add(axe);
		check(axe.doEquip(hero), "自然之斧无法装备");
		check(axe.actions(hero).contains(NaturalAxe.AC_HARVEST), "装备后没有开放收获动作");
	}

	private static void testHarvestAndGrowth() {
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.pos = 27;
		Dungeon.hero = hero;
		Arrays.fill(level.map, Terrain.EMPTY);
		Arrays.fill(level.passable, true);
		Arrays.fill(level.heroFOV, true);
		level.buildFlagMaps();

		NaturalAxe axe = new NaturalAxe();
		hero.belongings.backpack.items.add(axe);
		check(axe.doEquip(hero), "自然之斧无法装备");
		axe.activate(hero);
		check(NaturalAxe.growthOf(hero) != null, "装备自然之斧没有获得成长标记");

		level.map[hero.pos + 1] = Terrain.FURROWED_GRASS;
		level.map[hero.pos + 9] = Terrain.EMPTY;
		check(axe.harvestCell(hero.pos + 1), "自然之斧无法收获高草");
		check(level.map[hero.pos + 1] == Terrain.GRASS, "收获后高草地形没有变成草地");
		check(!axe.harvestCell(hero.pos + 9), "空地上错误地产生了收获");

		//半径 1：中心相邻格（hero.pos+2）在范围内，距离 2 格（hero.pos+3）不在
		level.map[hero.pos + 2] = Terrain.FURROWED_GRASS;
		level.map[hero.pos + 3] = Terrain.FURROWED_GRASS;
		int harvested = axe.harvestArea(hero, hero.pos + 1);
		check(harvested == 1, "半径 1 的范围收获数量错误：" + harvested);
		check(level.map[hero.pos + 2] == Terrain.GRASS, "半径 1 时相邻草地没有被收获");
		check(level.map[hero.pos + 3] == Terrain.FURROWED_GRASS, "超出半径的草地被错误收获");

		//收获带来的成长：exp 达到 level+1 即升级，半径随之变大
		check(axe.level() == 1 && axe.exp() == 0, "收获后没有按门槛升级：" + axe.level());
		check(axe.radius() == 2, "升级后收获半径没有扩大：" + axe.radius());

		harvested = axe.harvestArea(hero, hero.pos + 1);
		check(harvested == 1, "半径 2 时没有收获到距离 2 格的草地：" + harvested);
		check(level.map[hero.pos + 3] == Terrain.GRASS, "半径 2 时距离 2 格的草地没有被收获");
	}

	private static void testPoolAndMessages() throws Exception {
		boolean inPool = false;
		for (Class<?> type : Generator.Category.ARTIFACT.classes) {
			if (type == NaturalAxe.class) inPool = true;
		}
		check(inPool, "自然之斧没有进入普通神器池");
		check(Generator.Category.ARTIFACT.classes.length == Generator.Category.ARTIFACT.defaultProbs.length,
				"神器池与权重数组长度不一致");

		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			String text = read(file);
			check(text.contains("items.equipment.artifacts.naturalaxe.desc="), file + "缺少自然之斧描述");
			check(text.contains("items.equipment.artifacts.naturalaxe.ac_harvest="), file + "缺少收获动作名");
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

	private SpsNaturalAxeTest() { }
}
