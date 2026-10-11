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
import pd.items.consum.food.vegetable.Blandfruit;
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
			testSwallowSelector();
			testTimeRecharge();
			testFeastMovedToAcidFeastPotion();
			testSaveMigration();
			testLocalizedResources();
			System.out.println("SPS丰饶之角测试通过：吞噬食物按等级效率充能（实数不截断）、每18点自动凝干粮并成长（上限10级）、时间充能、盛宴移植强酸合剂、存档迁移及英文文本均正常。");
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
		check(HornOfPlenty.AC_SWALLOW.equals(horn.defaultAction()) && horn.levelCapValue() == 10
				&& horn.chargeCapValue() == 0, "号角默认动作、等级上限或充能上限错误");
		check(horn.actions(hero).contains(HornOfPlenty.AC_SWALLOW)
				&& !horn.actions(hero).contains("EAT") && !horn.actions(hero).contains("FEED"),
				"号角仍残留食用或盛宴动作");
		check("0".equals(horn.status()), "号角状态栏没有只显示充能数");

		//SPSEXPD: 0 级效率 75%；无味果 hornValue=2 -> 2×0.75 = 1.5 点（实数，不再被 int 截断）
		check(horn.efficiency() == 0.75f, "0 级号角的食物转化效率不是 75%：" + horn.efficiency());
		float gained = horn.swallow(hero, new Blandfruit());
		check(gained == 1.5f && horn.chargeValue() == 1.5f && horn.level() == 0 && countRations(hero) == 0,
				"吞噬无味果没有按 75% 效率得到 1.5 点充能，或不足 18 点就产出了干粮");
		check("1".equals(horn.status()), "号角状态栏没有向下取整显示充能数（应为1）");

		//SPSEXPD: 小饱食度食物的能量不再被丢弃——hornValue=1 在 75% 下是 0.75 点（旧实现会被截断成 0）
		TestHorn tiny = new TestHorn();
		tiny.identify();
		hero.belongings.artifact = tiny;
		check(tiny.swallow(hero, new Fruit()) == 0.75f && tiny.chargeValue() == 0.75f,
				"小饱食度食物的能量被截断丢弃了");

		//满 18 点自动凝成干粮并成长一级（0 级下 PerfectFood hornValue=10 -> 10×0.75 = 7.5 点/个）
		horn.swallow(hero, new pd.items.consum.food.completefood.PerfectFood());
		horn.swallow(hero, new pd.items.consum.food.completefood.PerfectFood());
		check(horn.chargeValue() == 16.5f && countRations(hero) == 0, "充能累计错误：" + horn.chargeValue());
		horn.swallow(hero, new pd.items.consum.food.completefood.PerfectFood());
		check(horn.chargeValue() == 6f && countRations(hero) == 1 && horn.level() == 1,
				"充能没有每18点自动凝成干粮、余数保留或每包成长1级");

		//满级后仍产干粮但不再成长
		horn.level(10);
		horn.setCharge(17f);
		horn.swallow(hero, new pd.items.consum.food.completefood.PerfectFood());
		check(horn.level() == 10 && countRations(hero) == 2,
				"满级后没有继续产出干粮或仍在成长");

		//SPSEXPD: 号角等级按内部计（0~10），不再折算成 0~10 的通用显示尺度
		check(horn.visiblyUpgraded() == horn.level() && horn.buffedVisiblyUpgraded() == horn.level(),
				"号角等级仍被折算成 0~10 显示尺度：" + horn.visiblyUpgraded());
		horn.level(8);
		check(horn.visiblyUpgraded() == 8, "号角 8 级没有按内部等级显示：" + horn.visiblyUpgraded());

		//SPSEXPD: 神器等级不受鉴定状态影响（未装备过的神器也要显示等级角标）
		TestHorn fresh = new TestHorn();
		fresh.level(3);
		check(!fresh.isIdentified() && fresh.visiblyUpgraded() == 3 && fresh.buffedVisiblyUpgraded() == 3,
				"未鉴定号角没有按内部等级显示：" + fresh.visiblyUpgraded());

		//普通神器仍按 0~10 折算，且同样不受鉴定状态影响
		pd.items.equipment.artifacts.ChaliceOfBlood chalice =
				new pd.items.equipment.artifacts.ChaliceOfBlood();
		chalice.level(5);
		check(!chalice.isIdentified() && chalice.visiblyUpgraded() == 5,
				"普通神器未鉴定时不显示等级或折算错误：" + chalice.visiblyUpgraded());
		chalice.level(10);
		check(chalice.visiblyUpgraded() == 10, "普通神器满级显示错误：" + chalice.visiblyUpgraded());
	}

	private static int countRations(Hero hero) {
		int count = 0;
		for (pd.items.Item item : hero.belongings.backpack.items) {
			if (item instanceof pd.items.consum.food.staplefood.NormalRation) count += item.quantity();
		}
		return count;
	}

	/**
	 * SPSEXPD: 号角交互——吞噬必须先设置 curItem（否则 onSelect 的守卫失败，吞噬毫无反应）、
	 * 选择窗默认打开购物车、干粮包/干粮小包不能喂。
	 */
	private static void testSwallowSelector() throws Exception {
		RecordingHero hero = prepareHero();
		TestHorn horn = new TestHorn();
		horn.identify();
		hero.belongings.artifact = horn;

		java.lang.reflect.Field curItemField = pd.items.Item.class.getDeclaredField("curItem");
		curItemField.setAccessible(true);
		curItemField.set(null, null);

		horn.execute(hero, HornOfPlenty.AC_SWALLOW);
		check(curItemField.get(null) == horn, "号角吞噬没有设置 curItem，onSelect 守卫会失败");

		java.lang.reflect.Field selectorField = HornOfPlenty.class.getDeclaredField("itemSelector");
		selectorField.setAccessible(true);
		pd.windows.WndBag.ItemSelector selector =
				(pd.windows.WndBag.ItemSelector) selectorField.get(null);

		check(selector.preferredBag() == pd.items.equipment.bags.ShoppingCart.class,
				"号角吞噬没有默认打开购物车");
		check(!selector.itemSelectable(new pd.items.consum.food.staplefood.NormalRation()),
				"干粮包仍然能喂给号角");
		check(!selector.itemSelectable(new pd.items.consum.food.staplefood.OverpricedRation()),
				"干粮小包仍然能喂给号角");
		check(selector.itemSelectable(new Blandfruit()), "无味果应该仍然能喂给号角");
	}

	private static void testTimeRecharge() {
		RecordingHero hero = prepareHero();
		TestHorn slow = new TestHorn();
		HornOfPlenty.hornRecharge slowRecharge = slow.new hornRecharge();
		check(slowRecharge.attachTo(hero), "零级号角充能状态无法附加");
		//SPSEXPD: 0 级每回合 (0.25+0)×2 = 0.5 → 160 回合攒够 80 点（1 充能）
		for (int i = 0; i < 159; i++) slowRecharge.act();
		check(slow.chargeValue() == 0f, "零级号角在160回合前提前产生食物");
		slowRecharge.act();
		check(slow.chargeValue() == 1f, "零级号角160回合没有产生一格食物");

		Actor.clear();
		hero = prepareHero();
		TestHorn fast = new TestHorn();
		fast.level(10);
		HornOfPlenty.hornRecharge fastRecharge = fast.new hornRecharge();
		check(fastRecharge.attachTo(hero), "满级号角充能状态无法附加");
		//SPSEXPD: 满级每回合 (0.25+0.45)×2 = 1.4 → 58 回合攒够 80 点（1 充能）
		//（上限 10 级、斜率 ×3，与旧版 30 级的满级速率一致）
		check(fast.efficiency() == 1.25f, "满级号角的食物转化效率不是 125%：" + fast.efficiency());
		for (int i = 0; i < 57; i++) fastRecharge.act();
		check(fast.chargeValue() == 0f, "满级号角在58回合前提前产生食物");
		fastRecharge.act();
		check(fast.chargeValue() == 1f, "满级号角58回合没有产生一格食物");

		fast.setCharge(17f);
		//SPSEXPD: 79 + 1.4 = 80.4 → 充能 18 点 → 自动凝 1 包、余 0
		fast.setPartial(79f);
		fastRecharge.act();
		check(fast.chargeValue() == 0f && countRations(hero) == 1 && fast.levelValue() == 10
				&& fast.partialValue() < 1f && fast.image == EquipmentJewelleryArtifactDict.ARTIFACT_HORN1,
				"号角满18充能没有自动凝成干粮、保留余数或按余数刷新图标");
		fast.cursed = true;
		fast.setCharge(17f);
		fast.setPartial(40f);
		fastRecharge.act();
		check(fast.chargeValue() == 17f && fast.partialValue() == 0f, "诅咒号角仍充能或未清空部分充能");
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
		//SPSEXPD: 旧档——充能写在基类 charge 里（单位是旧的 1/3），等级上限是旧的 30
		TestHorn oldPortHorn = new TestHorn();
		oldPortHorn.level(4);
		Bundle oldSave = new Bundle();
		oldPortHorn.storeInBundle(oldSave);
		oldSave.remove("horn_charge");
		oldSave.put("charge", 7);
		oldSave.put("stored", 150);

		TestHorn migrated = new TestHorn();
		migrated.restoreFromBundle(oldSave);
		//SPSEXPD: 旧 charge=7 → 新单位 21（>=15 即 HORN4）；等级 4*3+2=14 收敛到新上限 10
		check(migrated.level() == 10 && migrated.chargeValue() == 21f
				&& migrated.image == EquipmentJewelleryArtifactDict.ARTIFACT_HORN4,
				"早期10级号角的等级、储存进度、充能或图标迁移错误");
		Bundle newSave = new Bundle();
		migrated.storeInBundle(newSave);
		check(!newSave.contains("stored"), "新号角存档仍写入破碎版储存能量字段");
		check(newSave.contains("horn_charge"), "新号角存档没有写入实数充能字段");

		//SPSEXPD: 无破碎标记的 0.9.8 号角存档不被缩放（等级在新上限内则原样保留）
		TestHorn legacy = new TestHorn();
		legacy.level(8);
		Bundle legacySave = new Bundle();
		legacy.storeInBundle(legacySave);
		TestHorn restoredLegacy = new TestHorn();
		restoredLegacy.restoreFromBundle(legacySave);
		check(restoredLegacy.level() == 8, "无破碎标记的0.9.8号角存档被错误缩放");
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
		float chargeValue() { return hornCharge; }
		int chargeCapValue() { return chargeCap; }
		int levelCapValue() { return levelCap; }
		int levelValue() { return level(); }
		float partialValue() { return partialCharge; }
		float efficiency() { return conversionEfficiency(); }
		void setCharge(float value) { hornCharge = value; }
		void setPartial(float value) { partialCharge = value; }
	}

	private SpsHornOfPlentyTest() {
	}
}
