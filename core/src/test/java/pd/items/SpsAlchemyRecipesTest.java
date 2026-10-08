package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Recharging;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.items.equipment.artifacts.AlchemistsToolkit;
import pd.items.consum.brewed.Brewed;
import pd.items.consum.eggs.Egg;
import pd.items.consum.food.Blandfruit;
import pd.items.consum.food.FishCracker;
import pd.items.consum.food.Honey;
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.completefood.*;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.food.meatfood.MeatFood;
import pd.items.consum.food.staplefood.NormalRation;
import pd.items.consum.food.staplefood.OverpricedRation;
import pd.items.consum.food.staplefood.StapleFood;
import pd.items.consum.food.vegetable.NutVegetable;
import pd.items.consum.food.vegetable.Truffles;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.consum.medicine.*;
import pd.items.consum.potions.PotionOfFrost;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.consum.potions.PotionOfMixing;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.exotic.ExoticScroll;
import pd.items.equipment.weapon.missiles.arrows.*;
import pd.plants.*;
import pd.scenes.AlchemyScene;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public final class SpsAlchemyRecipesTest {

	private static final Class<?> S = StapleFood.class;
	private static final Class<?> V = Vegetable.class;
	private static final Class<?> M = MeatFood.class;
	private static final Class<?> W = WaterItem.class;
	private static final Class<?> O = StoneOre.class;
	private static final Class<?> N = Nut.class;
	private static final Class<?> F = Fruit.class;
	private static final Class<?> P = PotionOfHealing.class;
	private static final Class<?> R = ScrollOfIdentify.class;
	private static final Class<?> SEED = Icecap.Seed.class;

	private static final Object[][] CASES = {
			{PerfectFood.class, 1, V, O, S, W, F},
			{PerfectFood.class, 1, FishCracker.class},
			{TimePill.class, 1, O, O, O, O, W},
			{Crystalnucleus.class, 1, O, O, O, W, SEED},
			{Hamburger.class, 1, S, S, V, M, M},
			{Chocolate.class, 1, N, N, N, N, N},
			{OverpricedRation.class, 1, N, N, N, N},
			{RiceGruel.class, 2, S, W, W},
			{ZongZi.class, 1, S, V, M},
			{Greaterpill.class, 1, F, P, P},
			{RealgarWine.class, 1, W, Firebloom.Seed.class, Earthroot.Seed.class},
			{GreenSpore.class, 1, W, V, Dewcatcher.Seed.class},
			{GoldenJelly.class, 1, W, V, Stormvine.Seed.class},
			{Earthstar.class, 1, W, V, Earthroot.Seed.class},
			{JackOLantern.class, 1, W, V, Firebloom.Seed.class},
			{PixieParasol.class, 1, W, V, Dreamfoil.Seed.class},
			{BlueMilk.class, 1, W, V, Sungrass.Seed.class},
			{DeathCap.class, 1, W, V, Sorrowmoss.Seed.class},
			{Egg.class, 1, Honey.class, Gel.class, O},
			{Honey.class, 2, Honeypot.class},
			{Honey.class, 2, Honeypot.ShatteredPot.class},
			{Honey.class, 1, Truffles.class},
			{Icecream.class, 1, Honey.class, W, Icecap.Seed.class},
			{Porksoup.class, 1, M, W, V},
			{Foamedbeverage.class, 5, O, W, W, SEED, F},
			{Fruitsalad.class, 1, F, F, W},
			{Vegetablekebab.class, 1, V, V, M},
			{HoneyWater.class, 1, Honey.class, W, W},
			{Kebab.class, 1, V, M, M},
			{Vegetablesoup.class, 1, W, V, V},
			{NutCake.class, 1, S, N, Honey.class},
			{MoonCake.class, 1, S, N, N},
			{StoneOre.class, 1, N, N, N},
			{PetFood.class, 1, N, N, W},
			{FoodFans.class, 1, N, N, P},
			{Frenchfries.class, 1, N, N, R},
			{HoneyGel.class, 1, Honey.class, Gel.class},
			{Sishimi.class, 1, W, M},
			{Honeyrice.class, 1, Honey.class, S},
			{Honeymeat.class, 1, Honey.class, M},
			{Herbmeat.class, 1, M, SEED},
			{Chickennugget.class, 1, O, M},
			{Ricefood.class, 1, S, W},
			{Meatroll.class, 1, R, M},
			{Vegetableroll.class, 1, R, V},
			{Gel.class, 1, O, W},
			{NutVegetable.class, 1, N},
			//SPSEXPD: 无味果 + 水 → 干粮包；2 干粮包 → 3 干粮小包
			{NormalRation.class, 1, Blandfruit.class, W},
			{OverpricedRation.class, 3, NormalRation.class, NormalRation.class}
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		check(CASES.length == 49, "旧版确定性炼金配方数量错误");
		for (Object[] test : CASES) verifyCase(test);

		//SPSEXPD: 药剂酿造已由种子改为果实
		ArrayList<Item> seedpods = ingredients(Seedpod.Seed.class, Seedpod.Seed.class, Seedpod.Seed.class);
		check(Recipe.findRecipes(seedpods).get(0).sampleOutput(seedpods) instanceof Garbage,
				"三颗种子仍能酿造出药剂");
		ArrayList<Item> iceFruits = ingredients(IceFruit.class, IceFruit.class, IceFruit.class, IceFruit.class);
		for (Item item : iceFruits) item.quantity(2);
		Recipe fruitRecipe = Recipe.findRecipes(iceFruits).get(0);
		check(fruitRecipe.cost(iceFruits) == 0 && fruitRecipe.brew(iceFruits) instanceof PotionOfFrost,
				"四颗同种果实没有按材料对应药剂生成");
		for (Item item : iceFruits) check(item.quantity() == 1, "果实酿造耗材错误");
		ArrayList<Item> largeFruit = ingredients(LargeIceFruit.class, IceFruit.class);
		check(Recipe.findRecipes(largeFruit).get(0).brew(largeFruit) instanceof PotionOfFrost,
				"大型果实加普通果实没有按对应药剂生成");
		testBrewed();
		testExoticGuidePreviews();
		ArrayList<Item> invalid = ingredients(Gold.class, Gold.class, Gold.class, Gold.class, Gold.class);
		Recipe garbage = Recipe.findRecipes(invalid).get(0);
		Item waste = garbage.brew(invalid);
		//SPSEXPD: 垃圾图标已从占位换成材料图集的 SCRAP 废料
		check(waste instanceof Garbage && waste.quantity() == 5
				&& waste.image == pd.atlas.items.ConsumGoodsMaterialsMaterialsDict.SCRAP,
				"无效五槽组合没有生成五份废料垃圾");
		checkGarbageIcon();

		AlchemistsToolkit toolkit = new AlchemistsToolkit();
		//SPSEXPD: 炼金釜固定 5 格，不再依赖护腕等级
		check(AlchemyScene.spsInputCapacity(null) == 5, "普通炼金釜应为五槽");
		check(AlchemyScene.spsInputCapacity(toolkit) == 5, "零级炼金工具应为五槽");
		toolkit.upgrade(5);
		check(AlchemyScene.spsInputCapacity(toolkit) == 5, "五级炼金工具应为五槽");
		toolkit.upgrade(5);
		check(AlchemyScene.spsInputCapacity(toolkit) == 5, "十级炼金工具应为五槽");

		System.out.println("SPS炼金测试通过：47条固定配方、果实酿造、酿制无味果、无效组合垃圾、耗材、产量及固定五槽均正常。");
	}

	private static void checkGarbageIcon() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 848; y < 864; y++) {
			for (int x = 144; x < 160; x++) pixels.putInt(sheet.getRGB(x, y));
		}
		String hash = toHex(MessageDigest.getInstance("SHA-256").digest(pixels.array()));
		check("F735FA5C2AA745BB7F9EDF094320BE9BA4A03E7E403A8841D882ED0E0A4741A8".equals(hash),
				"垃圾图标与旧版像素不一致");
	}

	private static String toHex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void testBrewed() {
		//SPSEXPD: 无味果的浸泡(炖菜)配方已移除，直接构造炖菜验证其行为
		ArrayList<Item> inputs = ingredients(Blandfruit.class, Icecap.Seed.class);
		check(Recipe.findRecipes(inputs).get(0) == SpsAlchemyRecipes.garbageRecipe(),
				"无味果与冰冠花仍能生成酿制果");

		Brewed output = new Brewed().imbuePotion(new PotionOfFrost());
		check(output.potionAttrib instanceof PotionOfFrost, "炖菜的药剂属性未生效");

		Bundle bundle = new Bundle();
		output.storeInBundle(bundle);
		Brewed restored = new Brewed();
		restored.restoreFromBundle(bundle);
		check(restored.potionAttrib instanceof PotionOfFrost && restored.glowing() != null,
				"酿制无味果的药剂属性或光效没有保存");

		class TestBrewed extends Brewed {
			void potion(Hero hero) { applyPotionEffect(hero); }
			void heroClass(Hero hero) { applyClassEffect(hero); }
		}
		Hero hero = new Hero();
		hero.HTBoost = 80;
		hero.updateHT(false);
		hero.HP = 20;
		Dungeon.hero = hero;
		Buff.affect(hero, Poison.class);
		TestBrewed brewed = new TestBrewed();
		brewed.imbuePotion(new PotionOfHealing());
		brewed.potion(hero);
		check(hero.HP == hero.HT && hero.buff(Poison.class) == null, "阳光果没有完全治疗或净化");

		hero = new Hero();
		hero.heroClass = HeroClass.ROGUE;
		Dungeon.hero = hero;
		brewed.heroClass(hero);
		check(hero.buff(AttackUp.class) != null && hero.buff(AttackUp.class).level() == 30
				&& hero.buff(Recharging.class) == null, "盗贼酿制果职业收益错误");
	}

	private static void testExoticGuidePreviews() {
		//SPSXPD: 炼金指南「合成秘卷」页会遍历全部卷轴做预览，缺少合剂对应的卷轴（如测试卷轴）必须安全返回空
		ExoticScroll.ScrollToExotic recipe = new ExoticScroll.ScrollToExotic();
		for (Class<?> cls : Generator.Category.SCROLL.classes) {
			ArrayList<Item> in = ingredients(cls);
			Item out = recipe.sampleOutput(in);
			if (ExoticScroll.regToExo.containsKey(cls)) {
				check(out != null, "有合剂对应的卷轴预览为空：" + cls.getSimpleName());
			} else {
				check(out == null, "无合剂对应的卷轴应返回空预览：" + cls.getSimpleName());
			}
		}
	}

	private static void verifyCase(Object[] test) {
		Class<?> output = (Class<?>) test[0];
		int quantity = (Integer) test[1];
		Class<?>[] inputTypes = new Class<?>[test.length - 2];
		System.arraycopy(test, 2, inputTypes, 0, inputTypes.length);
		ArrayList<Item> ingredients = ingredients(inputTypes);
		for (Item item : ingredients) item.quantity(2);

		ArrayList<Recipe> recipes = Recipe.findRecipes(ingredients);
		check(recipes.size() == 1, "配方匹配数量错误：" + output.getSimpleName());
		Recipe recipe = recipes.get(0);
		check(recipe.cost(ingredients) == 0, "旧版配方不应消耗炼金能量：" + output.getSimpleName());
		Item sample = recipe.sampleOutput(ingredients);
		check(output.isInstance(sample) && sample.quantity() == quantity,
				"预览产物错误：" + output.getSimpleName());
		Item result = recipe.brew(ingredients);
		check(output.isInstance(result) && result.quantity() == quantity,
				"实际产物错误：" + output.getSimpleName());
		for (Item item : ingredients) {
			check(item.quantity() == 1, "材料没有逐槽消耗一份：" + output.getSimpleName());
		}
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Item> ingredients(Class<?>... types) {
		ArrayList<Item> result = new ArrayList<>();
		for (Class<?> type : types) result.add(Reflection.newInstance((Class<? extends Item>) type));
		return result;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsAlchemyRecipesTest() {
	}
}
