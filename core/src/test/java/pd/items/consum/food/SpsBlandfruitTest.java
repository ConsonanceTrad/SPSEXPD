package pd.items.consum.food;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.Recipe;
import pd.items.SpsAlchemyRecipes;
import pd.items.equipment.artifacts.HornOfPlenty;
import pd.items.consum.brewed.Brewed;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.potions.PotionOfFrost;
import pd.plants.Icecap;
import pd.sprites.CharSprite;
import pd.windows.WndBag;
import render.utils.serialize.Bundle;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's raw Blandfruit and separate Brewed item. */
public final class SpsBlandfruitTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testRawFruit();
			testOnlyBrewedRecipe();
			testOldCookedSaveCompatibility();
			testHornAcceptsRawFruit();
			testLocalizedResources();
			System.out.println("SPS无味果测试通过：生食、饱食、价值、水果分类、独立酿制果、旧存档、投掷、号角和四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.challenges = 0;
			setCurrentItem(null);
			app.exit();
		}
	}

	private static void testRawFruit() {
		Actor.clear();
		Dungeon.challenges = 0;
		Hero hero = new Hero();
		Dungeon.hero = hero;
		Hunger hunger = Buff.affect(hero, Hunger.class);
		hunger.affectHunger(-200f, true);

		TestBlandfruit fruit = new TestBlandfruit();
		check(fruit instanceof Fruit, "无味果没有归入旧版水果分类");
		check(Food.AC_EAT.equals(fruit.defaultAction()), "生无味果没有默认食用动作");
		check(fruit.energy == Hunger.HUNGRY && fruit.eatTime() == Food.TIME_TO_EAT,
				"生无味果应恢复100饱食并耗时3回合");
		check(fruit.value() == 20 && fruit.quantity(2).value() == 40, "无味果价值不是每个20");

		//SPSEXPD: 先把饥饿度抬高，确保能吃下的饱食量不受上限影响
		hunger.affectHunger(-300f, true);
		float beforeFeed = hunger.hunger();
		fruit.feed(hero);
		check(Math.abs((beforeFeed - hunger.hunger()) - Hunger.HUNGRY) < 1f,
				"生食无味果没有实际恢复干粮等量饱食");
		for (Method method : Blandfruit.class.getDeclaredMethods()) {
			check(!method.getName().equals("onThrow"), "无味果仍覆盖投掷并可能爆炸");
		}
	}

	private static void testOnlyBrewedRecipe() throws Exception {
		ArrayList<Item> inputs = new ArrayList<>();
		inputs.add(new Blandfruit());
		inputs.add(new Icecap.Seed());
		ArrayList<Recipe> recipes = Recipe.findRecipes(inputs);
		//SPSEXPD: 无味果的浸泡(炖菜)配方已移除，现在只会落入废料兜底
		check(recipes.size() == 1 && recipes.get(0) == SpsAlchemyRecipes.garbageRecipe(),
				"无味果与种子仍能匹配到炖菜配方");

		try {
			Class.forName(Blandfruit.class.getName() + "$CookFruit");
			throw new AssertionError("现代Blandfruit.CookFruit仍可进入配方系统");
		} catch (ClassNotFoundException expected) {
			// Expected: old saves only need Blandfruit and Chunks, not the removed recipe class.
		}
	}

	private static void testOldCookedSaveCompatibility() {
		Bundle oldSave = new Bundle();
		oldSave.put(Blandfruit.POTIONATTRIB, new PotionOfFrost());
		Blandfruit restored = new Blandfruit();
		restored.restoreFromBundle(oldSave);
		check(restored.potionAttrib instanceof PotionOfFrost && restored.glowing() != null,
				"早期SPS-SPD熟无味果的药剂属性或光效丢失");
		check(restored.defaultAction() != null && restored.energy == Hunger.STARVING,
				"早期SPS-SPD熟无味果不再可用或饱食值被破坏");

		Bundle roundTrip = new Bundle();
		restored.storeInBundle(roundTrip);
		Blandfruit secondRestore = new Blandfruit();
		secondRestore.restoreFromBundle(roundTrip);
		check(secondRestore.potionAttrib instanceof PotionOfFrost,
				"早期熟无味果再次保存后丢失药剂属性");
	}

	private static void testHornAcceptsRawFruit() throws Exception {
		Hero hero = new Hero();
		hero.sprite = new CharSprite() {
			@Override public void operate(int cell) { }
		};
		Dungeon.hero = hero;
		HornOfPlenty horn = new HornOfPlenty();
		Blandfruit fruit = new Blandfruit();
		hero.belongings.backpack.items.add(fruit);
		setCurrentItem(horn);

		Field selectorField = HornOfPlenty.class.getDeclaredField("itemSelector");
		selectorField.setAccessible(true);
		WndBag.ItemSelector selector = (WndBag.ItemSelector) selectorField.get(null);
		check(selector.itemSelectable(fruit), "魔法号角无法选择生无味果");
		selector.onSelect(fruit);
		check(!hero.belongings.backpack.items.contains(fruit), "魔法号角仍拒绝生无味果");

		check(horn.level() == 2, "魔法号角没有按旧版hornValue=2接收无味果");
	}

	private static void testLocalizedResources() throws Exception {
		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			required(items, "items.consum.food.blandfruit.name", file);
			required(items, "items.consum.food.blandfruit.desc", file);
			required(items, "items.consum.brewed.brewed.name", file);
			required(items, "items.consum.brewed.brewed.desc_cooked", file);
		}
		Properties simplified = load("messages/items/zh/items.properties");
		Properties traditional = load("messages/items/zh-hant/items.properties");
		check("无味果".equals(simplified.getProperty("items.consum.food.blandfruit.name")), "简体中文无味果文本乱码");
		check("無味果".equals(traditional.getProperty("items.consum.food.blandfruit.name")), "繁体中文无味果文本乱码");

		try (java.util.stream.Stream<Path> paths = java.nio.file.Files.walk(Path.of("messages"))) {
			for (Path path : (Iterable<Path>) paths.filter(p -> p.toString().endsWith(".properties"))::iterator) {
				String text = java.nio.file.Files.readString(path, StandardCharsets.UTF_8);
				check(!text.contains("\uFFFD"), "资源含替换字符：" + path);
			}
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

	private static String required(Properties properties, String key, String file) {
		String value = properties.getProperty(key);
		check(value != null && !value.isEmpty(), file + "缺少文本：" + key);
		return value;
	}

	private static void setCurrentItem(Item item) throws Exception {
		Field current = Item.class.getDeclaredField("curItem");
		current.setAccessible(true);
		current.set(null, item);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestBlandfruit extends Blandfruit {
		void feed(Hero hero) { satisfy(hero); }
		float eatTime() { return eatingTime(); }
	}

	private SpsBlandfruitTest() {
	}
}
