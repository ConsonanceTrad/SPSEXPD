package pd.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Feed;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.food.*;
import pd.items.food.completefood.CompleteFood;
import pd.items.food.fruit.Fruit;
import pd.items.food.fusion.Nut;
import pd.items.food.meatfood.MeatFood;
import pd.items.food.staplefood.OverpricedRation;
import pd.items.food.vegetable.BrewLeft;
import pd.items.food.vegetable.Vegetable;
import pd.sprites.ItemSpriteSheet;
import render.utils.Bundle;
import render.utils.Reflection;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

/** Runtime parity checks for SPS-PD 0.9.8's Horn of Plenty. */
public final class SpsHornOfPlentyTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		try {
			testFoodValues();
			testActionsAndFoodUpgrades();
			testTimeRecharge();
			testEatAllAndFeast();
			testSaveMigration();
			testLocalizedResources();
			System.out.println("SPS丰饶之角测试通过：30级成长、食物价值、时间充能、整角食用、耗竭盛宴、存档迁移及四语文本均符合0.9.8。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.challenges = 0;
			app.exit();
		}
	}

	private static void testFoodValues() {
		check(new Food().hornValue == 3, "普通食物的默认号角价值不是3");
		check(new Fruit().hornValue == 1 && new Vegetable().hornValue == 1
				&& new MeatFood().hornValue == 1, "水果、蔬菜或肉类的号角价值不是1");
		check(new Blandfruit().hornValue == 2 && new GoldenNut().hornValue == 2,
				"无味果或金色坚果的号角价值不是2");
		check(new BugMeat().hornValue == 1 && new MysteryMeat().hornValue == 1
				&& new SmallMeat().hornValue == 0, "独立肉类的号角价值错误");
		check(new WaterItem().hornValue == 0 && new Honey().hornValue == 0
				&& new FishCracker().hornValue == 0, "水、蜂蜜或鱼饼不应强化号角");
		check(new Nut().hornValue == 1 && new BrewLeft().hornValue == 0
				&& new OverpricedRation().hornValue == 2, "坚果、残渣或高价口粮的号角价值错误");
		check(new pd.items.food.meatfood.HarmPoop().hornValue == 0,
				"有害肉块不应强化号角");

		Object[][] completeFoods = {
				{"Chickennugget", 3}, {"Chocolate", 5}, {"FishPetFood", 1}, {"FoodFans", 2},
				{"Frenchfries", 2}, {"FruitCandy", 1}, {"Fruitsalad", 3}, {"Gel", 0},
				{"Hamburger", 6}, {"Herbmeat", 3}, {"HoneyGel", 2},
				{"Honeymeat", 3}, {"Honeyrice", 3}, {"HoneyWater", 2}, {"Icecream", 3},
				{"Kebab", 3}, {"Meatroll", 2}, {"Mediummeat", 3}, {"MixPizza", 2},
				{"MoonCake", 2}, {"NutCake", 3}, {"NutCookie", 1}, {"PerfectFood", 10},
				{"PetFood", 1}, {"Porksoup", 3}, {"Ricefood", 3}, {"RiceGruel", 2},
				{"Sishimi", 3}, {"Vegetablekebab", 2}, {"Vegetableroll", 2},
				{"Vegetablesoup", 3}, {"YearFood", 3}, {"ZongZi", 5}
		};
		for (Object[] expected : completeFoods) {
			String className = CompleteFood.class.getPackage().getName() + "." + expected[0];
			try {
				CompleteFood food = (CompleteFood) Reflection.newInstance((Class<?>) Class.forName(className));
				check(food.hornValue == (Integer) expected[1], "高级食物号角价值错误：" + expected[0]);
			} catch (ClassNotFoundException e) {
				throw new AssertionError("高级食物类缺失：" + expected[0], e);
			}
		}
	}

	private static void testActionsAndFoodUpgrades() {
		RecordingHero hero = prepareHero();
		TestHorn horn = new TestHorn();
		hero.belongings.artifact = horn;
		check(HornOfPlenty.AC_EAT.equals(horn.defaultAction()) && horn.levelCapValue() == 30
				&& horn.chargeCapValue() == 10, "号角默认动作、等级上限或容量错误");
		check(horn.actions(hero).contains(HornOfPlenty.AC_STORE)
				&& !horn.actions(hero).contains(HornOfPlenty.AC_FEED), "零级号角动作错误");
		check(!horn.actions(hero).contains("SNACK"), "破碎版浅尝动作仍进入SPS流程");

		horn.gainFoodValue(new Blandfruit());
		check(horn.level() == 2 && horn.actions(hero).contains(HornOfPlenty.AC_FEED),
				"无味果没有把号角提升2级或解锁盛宴");
		horn.level(29);
		horn.gainFoodValue(new pd.items.food.completefood.PerfectFood());
		check(horn.level() == 30 && !horn.actions(hero).contains(HornOfPlenty.AC_STORE),
				"号角没有在30级封顶或仍允许储存食物");
	}

	private static void testTimeRecharge() {
		RecordingHero hero = prepareHero();
		TestHorn slow = new TestHorn();
		HornOfPlenty.hornRecharge slowRecharge = slow.new hornRecharge();
		check(slowRecharge.attachTo(hero), "零级号角充能状态无法附加");
		for (int i = 0; i < 319; i++) slowRecharge.act();
		check(slow.chargeValue() == 0, "零级号角在320回合前提前产生食物");
		slowRecharge.act();
		check(slow.chargeValue() == 1, "零级号角320回合没有产生一格食物");

		Actor.clear();
		hero = prepareHero();
		TestHorn fast = new TestHorn();
		fast.level(30);
		HornOfPlenty.hornRecharge fastRecharge = fast.new hornRecharge();
		check(fastRecharge.attachTo(hero), "满级号角充能状态无法附加");
		for (int i = 0; i < 114; i++) fastRecharge.act();
		check(fast.chargeValue() == 0, "满级号角在115回合前提前产生食物");
		fastRecharge.act();
		check(fast.chargeValue() == 1, "满级号角115回合没有产生一格食物");

		fast.setCharge(9);
		fast.setPartial(79.5f);
		fastRecharge.act();
		check(fast.chargeValue() == 10 && fast.partialValue() == 0f
				&& fast.image == ItemSpriteSheet.ARTIFACT_HORN4, "号角满充、清零或图标阈值错误");
		fast.cursed = true;
		fast.setCharge(5);
		fast.setPartial(40f);
		fastRecharge.act();
		check(fast.chargeValue() == 5 && fast.partialValue() == 0f, "诅咒号角仍充能或未清空部分充能");
	}

	private static void testEatAllAndFeast() {
		RecordingHero hero = prepareHero();
		TestHorn horn = new TestHorn();
		hero.belongings.artifact = horn;
		Hunger hunger = Buff.affect(hero, Hunger.class);
		hunger.affectHunger(-450f, true);
		horn.setCharge(3);
		Statistics.reset();
		horn.execute(hero, HornOfPlenty.AC_EAT);
		check(hunger.hunger() == 330 && horn.chargeValue() == 0 && hero.spent == 3f,
				"号角没有一次吃光3格、恢复120饱食或消耗3回合");
		check(Statistics.foodEaten == 1 && horn.image == ItemSpriteSheet.ARTIFACT_HORN1,
				"三格号角食物没有计为一餐或图标未复原");

		Actor.clear();
		hero = prepareHero();
		horn = new TestHorn();
		hero.belongings.artifact = horn;
		horn.setCharge(2);
		Statistics.reset();
		horn.execute(hero, HornOfPlenty.AC_EAT);
		check(Statistics.foodEaten == 0 && horn.chargeValue() == 0, "少于三格的号角食物被错误计为正餐");

		Actor.clear();
		hero = prepareHero();
		horn = new TestHorn();
		hero.belongings.artifact = horn;
		horn.level(30);
		hero.spent = 0;
		horn.execute(hero, HornOfPlenty.AC_FEED);
		Feed feed = hero.buff(Feed.class);
		check(feed != null && feed.visualcooldown() >= 89f && horn.level() == 0 && hero.spent == 1f,
				"满级号角没有转化为90回合Feed、归零等级或消耗1回合");
	}

	private static void testSaveMigration() {
		TestHorn oldPortHorn = new TestHorn();
		oldPortHorn.level(4);
		oldPortHorn.setCharge(7);
		Bundle oldSave = new Bundle();
		oldPortHorn.storeInBundle(oldSave);
		oldSave.put("stored", 150);

		TestHorn migrated = new TestHorn();
		migrated.restoreFromBundle(oldSave);
		check(migrated.level() == 14 && migrated.chargeValue() == 7
				&& migrated.image == ItemSpriteSheet.ARTIFACT_HORN3,
				"早期10级号角的等级、储存进度、充能或图标迁移错误");
		Bundle newSave = new Bundle();
		migrated.storeInBundle(newSave);
		check(!newSave.contains("stored"), "新号角存档仍写入破碎版储存能量字段");

		TestHorn legacy = new TestHorn();
		legacy.level(20);
		Bundle legacySave = new Bundle();
		legacy.storeInBundle(legacySave);
		TestHorn restoredLegacy = new TestHorn();
		restoredLegacy.restoreFromBundle(legacySave);
		check(restoredLegacy.level() == 20, "无破碎标记的0.9.8号角存档被错误缩放");
	}

	private static void testLocalizedResources() throws Exception {
		for (String file : new String[]{"en/items.properties", "zh/items.properties",
				"zh-hant/items.properties", "ru/items.properties"}) {
			Properties items = load("messages/items/" + file);
			for (String key : new String[]{"name", "ac_eat", "ac_store", "ac_feed", "eat", "prompt",
					"no_food", "full", "maxlevel", "levelup", "desc", "desc_hint", "desc_cursed"}) {
				required(items, "items.artifacts.hornofplenty." + key, file);
			}
		}
		Properties zh = load("messages/items/zh/items.properties");
		check("耗竭-盛宴".equals(zh.getProperty("items.artifacts.hornofplenty.ac_feed")),
				"丰饶之角简体中文盛宴动作乱码或错误");
		try (java.util.stream.Stream<Path> paths = java.nio.file.Files.walk(Path.of("messages"))) {
			for (Path path : (Iterable<Path>) paths.filter(p -> p.toString().endsWith(".properties"))::iterator) {
				check(!java.nio.file.Files.readString(path, StandardCharsets.UTF_8).contains("\uFFFD"),
						"资源含替换字符：" + path);
			}
		}
	}

	private static RecordingHero prepareHero() {
		Actor.clear();
		Dungeon.challenges = 0;
		RecordingHero hero = new RecordingHero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		return hero;
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
		check(properties.getProperty(key) != null && !properties.getProperty(key).isEmpty(), file + "缺少文本：" + key);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class RecordingHero extends Hero {
		float spent;
		@Override public void spend(float time) { spent += time; }
	}

	private static final class TestHorn extends HornOfPlenty {
		int chargeValue() { return charge; }
		int chargeCapValue() { return chargeCap; }
		int levelCapValue() { return levelCap; }
		float partialValue() { return partialCharge; }
		void setCharge(int value) { charge = value; }
		void setPartial(float value) { partialCharge = value; }
	}

	private SpsHornOfPlentyTest() {
	}
}
