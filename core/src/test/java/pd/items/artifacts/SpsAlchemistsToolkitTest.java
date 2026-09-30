package pd.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.potions.PotionOfExperience;
import pd.items.potions.PotionOfFrost;
import pd.items.potions.PotionOfHaste;
import pd.items.potions.PotionOfHealing;
import pd.items.potions.PotionOfMight;
import pd.items.potions.PotionOfOverHealing;
import pd.items.potions.PotionOfStrength;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.scenes.AlchemyScene;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's Alchemist's Toolkit. */
public final class SpsAlchemistsToolkitTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testCombinationAndActions();
			testExhaustedCreation();
			testScoringAndEfficiency();
			testSaveCompatibility();
			testLocalizedResources();
			System.out.println("SPS炼金工具箱测试通过：三药顺序、动作、0至10级评分、种子效率、存档兼容和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testCombinationAndActions() {
		AlchemistsToolkit toolkit = new AlchemistsToolkit();
		check(toolkit.combination.size() == 3 && new HashSet<>(toolkit.combination).size() == 3,
				"炼金工具箱没有生成三种互不重复的药剂组合");
		check(!toolkit.combination.contains(PotionOfExperience.class)
				&& !toolkit.combination.contains(PotionOfOverHealing.class)
				&& !toolkit.combination.contains(PotionOfStrength.class)
				&& !toolkit.combination.contains(PotionOfMight.class), "工具箱组合包含源码禁用药剂");

		Hero hero = prepareHero();
		check(AlchemistsToolkit.AC_BREW.equals(toolkit.defaultAction()), "工具箱默认动作不是组合");
		check(toolkit.actions(hero).contains(AlchemistsToolkit.AC_COOKING)
				&& !toolkit.actions(hero).contains(AlchemistsToolkit.AC_BREW)
				&& !toolkit.actions(hero).contains(AlchemistsToolkit.AC_CREATE), "未装备零级工具箱动作错误");
		hero.belongings.artifact = toolkit;
		check(toolkit.actions(hero).contains(AlchemistsToolkit.AC_BREW)
				&& !toolkit.actions(hero).contains(AlchemistsToolkit.AC_CREATE), "已装备工具箱没有组合动作");
		toolkit.cursed = true;
		check(!toolkit.actions(hero).contains(AlchemistsToolkit.AC_BREW), "诅咒工具箱仍允许组合");
		toolkit.cursed = false;
		toolkit.level(4);
		hero.belongings.artifact = null;
		check(toolkit.actions(hero).contains(AlchemistsToolkit.AC_CREATE), "卸下的已升级工具箱没有耗竭造物");
		check(AlchemyScene.spsInputCapacity(toolkit) == 3, "四级工具箱错误增加了炼金槽位");
		toolkit.level(5);
		check(AlchemyScene.spsInputCapacity(toolkit) == 4, "五级工具箱没有增加到四个炼金槽位");
		toolkit.level(10);
		check(AlchemyScene.spsInputCapacity(toolkit) == 5, "十级工具箱没有增加到五个炼金槽位");
	}

	private static void testScoringAndEfficiency() {
		AlchemistsToolkit toolkit = fixedToolkit();
		toolkit.curGuess.add(PotionOfFrost.class);
		toolkit.curGuess.add(PotionOfHaste.class);
		toolkit.curGuess.add(PotionOfHealing.class);
		toolkit.guessBrew();
		check(toolkit.level() == 3 && toolkit.numWrongPlace == 3 && toolkit.numRight == 0,
				"三瓶位置全错没有得到3级评分");
		check(toolkit.bstGuess.size() == 3 && toolkit.curGuess.isEmpty(), "最佳组合或当前组合没有正确更新");

		toolkit.curGuess.addAll(toolkit.combination);
		toolkit.guessBrew();
		check(toolkit.level() == 10 && toolkit.bstGuess.isEmpty(), "三瓶完全正确没有从9分提升为满级10分");
		check(toolkit.availableEnergy() == 0 && toolkit.consumeEnergy(7) == 7,
				"破碎版炼金能量仍在工具箱上生效");

		AlchemistsToolkit.alchemy efficiency = toolkit.new alchemy();
		for (int i = 0; i < 100; i++) {
			check(efficiency.tryCook(2), "满级工具箱要求超过两颗种子");
		}
		toolkit.level(0);
		for (int i = 0; i < 100; i++) {
			check(efficiency.tryCook(3), "零级工具箱要求超过三颗种子");
		}
	}

	private static void testExhaustedCreation() {
		RecordingHero hero = prepareHero();
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		hero.pos = 20;
		TestToolkit toolkit = new TestToolkit();
		toolkit.level(4);
		check(toolkit.collect(hero.belongings.backpack), "造物测试工具箱无法放入背包");
		toolkit.execute(hero, AlchemistsToolkit.AC_CREATE);
		check(level.dropped == 4, "耗竭造物没有按工具箱等级生成物品");
		check(!hero.belongings.backpack.contains(toolkit) && hero.spent == 1f,
				"耗竭造物没有销毁工具箱或消耗一回合");
	}

	private static void testSaveCompatibility() {
		AlchemistsToolkit source = fixedToolkit();
		source.level(6);
		source.curGuess.add(PotionOfHealing.class);
		source.bstGuess.addAll(source.combination);
		source.numWrongPlace = 2;
		source.numRight = 1;
		Bundle bundle = new Bundle();
		source.storeInBundle(bundle);

		AlchemistsToolkit restored = new AlchemistsToolkit();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 6 && restored.combination.equals(source.combination)
				&& restored.curGuess.equals(source.curGuess) && restored.bstGuess.equals(source.bstGuess),
				"工具箱组合、等级或猜测没有随存档恢复");
		check(restored.numWrongPlace == 2 && restored.numRight == 1, "工具箱评分没有随存档恢复");

		AlchemistsToolkit modern = new AlchemistsToolkit();
		java.util.ArrayList<Class<?>> generated = new java.util.ArrayList<>(modern.combination);
		modern.restoreFromBundle(new Bundle());
		check(modern.combination.equals(generated), "早期破碎版无组合存档清空了新生成的安全组合");
		check(modern.availableEnergy() == 0, "早期破碎版存档保留了炼金能量");
	}

	private static void testLocalizedResources() throws Exception {
		String[] keys = {"name", "ac_brew", "ac_cooking", "ac_create", "prompt", "waste", "prefect",
				"bestbrew", "bdorder", "right", "desc", "desc_cursed", "level_zero", "level_ten",
				"make_from", "need_fix", "addpotion", "have_add", "know_first"};
		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : keys) required(items, "items.artifacts.alchemiststoolkit." + key, file);
			check(!items.containsKey("items.artifacts.alchemiststoolkit.ac_energize")
					&& !items.containsKey("items.artifacts.alchemiststoolkit.desc_warming"),
					file + "仍含破碎版供能或预热文本");
		}
		Properties zh = load("messages/items/zh/items.properties");
		check("组合".equals(zh.getProperty("items.artifacts.alchemiststoolkit.ac_brew"))
				&& "耗竭-造物".equals(zh.getProperty("items.artifacts.alchemiststoolkit.ac_create")),
				"炼金工具箱简体中文动作乱码或错误");
		try (java.util.stream.Stream<Path> paths = java.nio.file.Files.walk(Path.of("messages"))) {
			for (Path path : (Iterable<Path>) paths.filter(p -> p.toString().endsWith(".properties"))::iterator) {
				check(!java.nio.file.Files.readString(path, StandardCharsets.UTF_8).contains("\uFFFD"),
						"资源含替换字符：" + path);
			}
		}
	}

	private static AlchemistsToolkit fixedToolkit() {
		AlchemistsToolkit toolkit = new AlchemistsToolkit();
		toolkit.combination.clear();
		toolkit.combination.add(PotionOfHealing.class);
		toolkit.combination.add(PotionOfFrost.class);
		toolkit.combination.add(PotionOfHaste.class);
		return toolkit;
	}

	private static RecordingHero prepareHero() {
		Actor.clear();
		RecordingHero hero = new RecordingHero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static final class RecordingHero extends Hero {
		float spent;
		@Override public void spendAndNext(float time) { spent += time; }
	}

	private static final class TestToolkit extends AlchemistsToolkit {
		@Override protected Item creationItem() { return new Gold(); }
	}

	private static final class RecordingLevel extends Level {
		int dropped;
		RecordingLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>(); heaps = new SparseArray<>(); blobs = new HashMap<>();
			plants = new SparseArray<Plant>(); traps = new SparseArray<Trap>(); transitions = new ArrayList<>();
			customTiles = new ArrayList<>(); customTerrain = new ArrayList<>(); customWalls = new ArrayList<>();
			buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			dropped += item.quantity();
			Heap heap = heaps.get(cell);
			if (heap == null) { heap = new Heap(); heap.pos = cell; heaps.put(cell, heap); }
			heap.drop(item);
			return heap;
		}
	}

	private static Properties load(String path) throws Exception {
		Properties properties = new Properties();
		try (InputStreamReader reader = new InputStreamReader(
				java.nio.file.Files.newInputStream(Path.of(path)), StandardCharsets.UTF_8)) {
			properties.load(reader);
		}
		return properties;
	}

	private static void required(Properties properties, String key, String file) {
		check(properties.getProperty(key) != null && !properties.getProperty(key).isEmpty(),
				file + "缺少文本：" + key);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsAlchemistsToolkitTest() {
	}
}
