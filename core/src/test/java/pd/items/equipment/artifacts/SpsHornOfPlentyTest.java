package pd.items.equipment.artifacts;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

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
import pd.items.consum.food.*;
import pd.items.consum.food.completefood.CompleteFood;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.food.meatfood.MeatFood;
import pd.items.consum.food.staplefood.OverpricedRation;
import pd.items.consum.food.vegetable.BrewLeft;
import pd.items.consum.food.vegetable.Vegetable;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

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
			testSwallowAndRationConversion();
			testTimeRecharge();
			testFeastMovedToAcidFeastPotion();
			testSaveMigration();
			testLocalizedResources();
			System.out.println("SPS丰饶之角测试通过：吞噬食物充能、每6点自动凝干粮并成长、时间充能、盛宴移植强酸合剂、存档迁移及英文文本均正常。");
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
		check(new pd.items.consum.food.meatfood.HarmPoop().hornValue == 0,
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

	//SPSEXPD: 丰饶之角改造——吞噬食物换充能、每 6 点自动凝成干粮并成长
	private static void testSwallowAndRationConversion() {
		RecordingHero hero = prepareHero();
		TestHorn horn = new TestHorn();
		horn.identify();
		hero.belongings.artifact = horn;
		check(HornOfPlenty.AC_SWALLOW.equals(horn.defaultAction()) && horn.levelCapValue() == 30
				&& horn.chargeCapValue() == 0, "号角默认动作、等级上限或充能上限错误");
		check(horn.actions(hero).contains(HornOfPlenty.AC_SWALLOW)
				&& !horn.actions(hero).contains("EAT") && !horn.actions(hero).contains("FEED"),
				"号角仍残留食用或盛宴动作");
		check("0".equals(horn.status()), "号角状态栏没有只显示充能数");

		//吞噬食物按 hornValue 加充能（无味果 hornValue=2），不足 6 不产干粮
		horn.swallow(hero, new Blandfruit());
		check(horn.chargeValue() == 2 && horn.level() == 0 && countRations(hero) == 0,
				"吞噬无味果没有+2充能，或不足6点就产出了干粮");
		check("2".equals(horn.status()), "号角状态栏没有显示充能数2");

		//满 6 点自动凝成干粮并成长一级（吞 PerfectFood hornValue=10：10+2=12 → 2 包干粮 + 2 级，余 0）
		horn.swallow(hero, new pd.items.consum.food.completefood.PerfectFood());
		check(horn.chargeValue() == 0 && countRations(hero) == 2 && horn.level() == 2,
				"充能没有每6点自动凝成干粮、余数保留或每包成长1级");

		//满级后仍产干粮但不再成长
		horn.level(30);
		horn.swallow(hero, new pd.items.consum.food.completefood.PerfectFood());
		check(horn.level() == 30 && countRations(hero) == 3 && horn.chargeValue() == 4,
				"满级后没有继续产出干粮或仍在成长");
	}

	private static int countRations(Hero hero) {
		int count = 0;
		for (pd.items.Item item : hero.belongings.backpack.items) {
			if (item instanceof pd.items.consum.food.staplefood.NormalRation) count += item.quantity();
		}
		return count;
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

		fast.setCharge(5);
		fast.setPartial(79.5f);
		fastRecharge.act();
		//SPSEXPD: 充能 5+1=6 → 自动凝成 1 包干粮、余 0；满级不再成长；部分充能保留小数余量
		check(fast.chargeValue() == 0 && countRations(hero) == 1 && fast.levelValue() == 30
				&& fast.partialValue() < 1f && fast.image == EquipmentJewelleryArtifactDict.ARTIFACT_HORN1,
				"号角满6充能没有自动凝成干粮、保留余数或按余数刷新图标");
		fast.cursed = true;
		fast.setCharge(5);
		fast.setPartial(40f);
		fastRecharge.act();
		check(fast.chargeValue() == 5 && fast.partialValue() == 0f, "诅咒号角仍充能或未清空部分充能");
	}

	//SPSEXPD: 盛宴效果已移植到吞星花对应的合剂（强酸盛宴合剂）
	private static void testFeastMovedToAcidFeastPotion() {
		RecordingHero hero = prepareHero();
		pd.items.consum.potions.exotic.PotionOfAcidFeast potion = new pd.items.consum.potions.exotic.PotionOfAcidFeast();
		potion.apply(hero);
		Feed feed = hero.buff(Feed.class);
		check(feed != null && feed.visualcooldown() >= 49f,
				"强酸盛宴合剂没有给予50回合生命摄取");

		check(pd.items.consum.potions.exotic.ExoticPotion.regToExo.get(pd.items.consum.potions.PotionOfAcid.class)
				== pd.items.consum.potions.exotic.PotionOfAcidFeast.class,
				"强酸药剂没有登记合剂升级");
		check(pd.items.LargeFruitToElixir.types.get(pd.items.equipment.weapon.missiles.arrows.LargeStarEaterFruit.class)
				== pd.items.consum.potions.exotic.PotionOfAcidFeast.class,
				"吞星花大果没有对应强酸盛宴合剂");
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
		//SPSEXPD: 旧档 charge=7 → 新图标阈值（>=5 即 HORN4），充能会在下次 tick 自动凝成干粮
		check(migrated.level() == 14 && migrated.chargeValue() == 7
				&& migrated.image == EquipmentJewelleryArtifactDict.ARTIFACT_HORN4,
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
		//SPSEXPD: 简体内联在代码里（InlineText），zh-hant/其它语言目录不维护——只核对英文模板
		Properties items = load("messages/items/en/items.properties");
		for (String key : new String[]{"name", "ac_swallow", "prompt", "swallow", "ration",
				"levelup", "maxlevel", "desc", "desc_hint", "desc_cursed"}) {
			required(items, "items.equipment.artifacts.hornofplenty." + key, "en/items.properties");
		}
		for (String key : new String[]{"name", "desc", "feast"}) {
			required(items, "items.consum.potions.exotic.potionofacidfeast." + key, "en/items.properties");
		}
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
		int levelValue() { return level(); }
		float partialValue() { return partialCharge; }
		void setCharge(int value) { charge = value; }
		void setPartial(float value) { partialCharge = value; }
	}

	private SpsHornOfPlentyTest() {
	}
}
