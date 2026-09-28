package com.shatteredpixel.shatteredpixeldungeon.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.brewed.Brewed;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.Egg;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FishCracker;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Honey;
import com.shatteredpixel.shatteredpixeldungeon.items.food.WaterItem;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.*;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Fruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fusion.Nut;
import com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood.MeatFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.OverpricedRation;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.StapleFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.NutVegetable;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.Truffles;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.Vegetable;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMixing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.plants.*;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.watabou.utils.Reflection;
import com.watabou.utils.Bundle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;

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
			{Powerpill.class, 1, M, M, M, V},
			{Hardpill.class, 1, M, M, O, V},
			{Smashpill.class, 1, M, M, P, V},
			{Shootpill.class, 1, M, M, M, SEED},
			{Musicpill.class, 1, M, M, O, SEED},
			{MagicPill.class, 1, M, M, P, SEED},
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
			{PotionOfMixing.class, 1, Seedpod.Seed.class, Seedpod.Seed.class, Seedpod.Seed.class},
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
			{NutVegetable.class, 1, N}
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		check(CASES.length == 54, "旧版确定性炼金配方数量错误");
		for (Object[] test : CASES) verifyCase(test);

		ArrayList<Item> seedpods = ingredients(Seedpod.Seed.class, Seedpod.Seed.class, Seedpod.Seed.class);
		check(Recipe.findRecipes(seedpods).size() == 1
				&& Recipe.findRecipes(seedpods).get(0).sampleOutput(seedpods) instanceof PotionOfMixing,
				"三颗种荚没有优先生成混合药剂");
		ArrayList<Item> iceSeeds = ingredients(Icecap.Seed.class, Icecap.Seed.class, Icecap.Seed.class);
		for (Item item : iceSeeds) item.quantity(2);
		Recipe seedRecipe = Recipe.findRecipes(iceSeeds).get(0);
		check(seedRecipe.cost(iceSeeds) == 0 && seedRecipe.brew(iceSeeds) instanceof PotionOfFrost,
				"旧版三种子制药没有按材料对应药剂生成");
		for (Item item : iceSeeds) check(item.quantity() == 1, "三种子制药耗材错误");
		testBrewed();
		ArrayList<Item> invalid = ingredients(Gold.class, Gold.class, Gold.class, Gold.class, Gold.class);
		Recipe garbage = Recipe.findRecipes(invalid).get(0);
		Item waste = garbage.brew(invalid);
		check(waste instanceof Garbage && waste.quantity() == 5
				&& waste.image == com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.SPS_GARBAGE,
				"无效五槽组合没有生成五份旧版图标垃圾");
		checkGarbageIcon();

		AlchemistsToolkit toolkit = new AlchemistsToolkit();
		check(AlchemyScene.spsInputCapacity(null) == 3, "普通炼金釜应为三槽");
		check(AlchemyScene.spsInputCapacity(toolkit) == 3, "零级炼金工具应为三槽");
		toolkit.upgrade(5);
		check(AlchemyScene.spsInputCapacity(toolkit) == 4, "五级炼金工具应为四槽");
		toolkit.upgrade(5);
		check(AlchemyScene.spsInputCapacity(toolkit) == 5, "十级炼金工具应为五槽");

		System.out.println("SPS炼金测试通过：54条固定配方、三种子制药、酿制无味果、无效组合垃圾、耗材、产量及3/4/5槽解锁均正常。");
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
		ArrayList<Item> inputs = ingredients(Blandfruit.class, Icecap.Seed.class);
		Recipe recipe = Recipe.findRecipes(inputs).get(0);
		check(recipe.cost(inputs) == 0, "酿制无味果不应消耗炼金能量");
		Item output = recipe.brew(inputs);
		check(output instanceof Brewed && ((Brewed) output).potionAttrib instanceof PotionOfFrost,
				"无味果与冰冠花没有生成冰霜酿制果");
		check(inputs.get(0).quantity() == 0 && inputs.get(1).quantity() == 0, "酿制无味果耗材错误");

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
