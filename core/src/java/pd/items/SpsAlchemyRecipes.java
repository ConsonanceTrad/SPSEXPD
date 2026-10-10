/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.ShatteredPixelDungeon;
import pd.items.consum.eggs.Egg;
import pd.items.consum.food.FishCracker;
import pd.items.consum.food.Honey;
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.completefood.*;
import pd.items.consum.food.fruit.*;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.food.meatfood.MeatFood;
import pd.items.consum.food.processed.*;
import pd.items.consum.food.staplefood.NormalRation;
import pd.items.consum.food.vegetable.Blandfruit;
import pd.items.consum.food.staplefood.OverpricedRation;
import pd.items.consum.food.staplefood.StapleFood;
import pd.items.consum.food.vegetable.*;
import pd.items.consum.medicine.*;
import pd.items.consum.materials.DryTwig;
import pd.items.consum.materials.FreshGrass;
import pd.items.consum.materials.Tinder;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionOfConfusion;
import pd.items.consum.potions.elixirs.WishPotion;
import pd.items.consum.scrolls.Scroll;
import pd.items.misc.WallCalendar;
import pd.plants.*;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** The deterministic recipe chain from SPS-PD 0.9.8's WndAlchemy. */
public final class SpsAlchemyRecipes {

	/** SPSEXPD: 许愿魔药的确定性配方——炼金釜与炼金指南共用这一份定义，避免两处漂移。 */
	public static final TypedRecipe WISH_POTION = recipe(WishPotion.class,
			PotionOfConfusion.class, WishPetal.class, CrystalShard.class, AetherLiquid.class, HighEnergySpore.class);

	/** SPSEXPD: 无味果 + 水 → 干粮包（「无味果 → 果丝」的单材料配方保持不变，两者不冲突）。 */
	public static final TypedRecipe BLANDFRUIT_TO_RATION = recipe(NormalRation.class, Blandfruit.class, WaterItem.class);

	/** SPSEXPD: 2 干粮包 → 3 干粮小包（饱食度守恒：600 = 3 × 200）。 */
	public static final TypedRecipe RATION_TO_SMALL = recipe(3, OverpricedRation.class,
			NormalRation.class, NormalRation.class);

	/** SPSEXPD: 通用确定性配方：产物 + 若干输入，数量与顺序无关全匹配。 */
	public static final class TypedRecipe extends Recipe {
		private final Class<?>[] inputs;
		private final Class<? extends Item> output;
		private final int outputQuantity;

		TypedRecipe(Class<? extends Item> output, int outputQuantity, Class<?>... inputs) {
			this.inputs = inputs;
			this.output = output;
			this.outputQuantity = outputQuantity;
		}

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			return ingredients.size() == inputs.length
					&& matches(ingredients, 0, new boolean[ingredients.size()]);
		}

		private boolean matches(ArrayList<Item> ingredients, int input, boolean[] used) {
			if (input == inputs.length) return true;
			for (int i = 0; i < ingredients.size(); i++) {
				if (!used[i] && inputs[input].isInstance(ingredients.get(i))) {
					used[i] = true;
					if (matches(ingredients, input + 1, used)) return true;
					used[i] = false;
				}
			}
			return false;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			for (Item ingredient : ingredients) {
				ingredient.quantity(ingredient.quantity() - 1);
			}
			return sampleOutput(ingredients);
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			try {
				Item result = Reflection.newInstance(output);
				result.quantity(outputQuantity);
				return result;
			} catch (Exception exception) {
				ShatteredPixelDungeon.reportException(exception);
				return null;
			}
		}

		/** SPSEXPD: 供炼金指南读取展示（指南与配方表共用同一份定义，避免两处漂移）。 */
		public Class<?>[] inputs(){
			return inputs;
		}

		public Class<? extends Item> output(){
			return output;
		}

		public int outputQuantity(){
			return outputQuantity;
		}
	}

	public static TypedRecipe recipe(Class<? extends Item> output, Class<?>... inputs) {
		return new TypedRecipe(output, 1, inputs);
	}

	public static TypedRecipe recipe(int quantity, Class<? extends Item> output, Class<?>... inputs) {
		return new TypedRecipe(output, quantity, inputs);
	}

	// This order is behavioral: it is the original else-if chain, including
	// Seedpod before the generic three-seed potion recipe in Recipe.
	private static final List<Recipe> RECIPES = Arrays.asList(
			recipe(PerfectFood.class, Vegetable.class, StoneOre.class, StapleFood.class, WaterItem.class, Fruit.class),
			recipe(PerfectFood.class, FishCracker.class),
			recipe(TimePill.class, StoneOre.class, StoneOre.class, StoneOre.class, StoneOre.class, WaterItem.class),
			recipe(Crystalnucleus.class, StoneOre.class, StoneOre.class, StoneOre.class, WaterItem.class, Plant.Seed.class),
			recipe(Hamburger.class, StapleFood.class, StapleFood.class, Vegetable.class, MeatFood.class, MeatFood.class),
			recipe(Chocolate.class, Nut.class, Nut.class, Nut.class, Nut.class, Nut.class),
			recipe(OverpricedRation.class, Nut.class, Nut.class, Nut.class, Nut.class),
			recipe(2, RiceGruel.class, StapleFood.class, WaterItem.class, WaterItem.class),
			recipe(ZongZi.class, StapleFood.class, Vegetable.class, MeatFood.class),
			recipe(Greaterpill.class, Fruit.class, Potion.class, Potion.class),
			recipe(RealgarWine.class, WaterItem.class, Firebloom.Seed.class, Earthroot.Seed.class),
			recipe(GreenSpore.class, WaterItem.class, Vegetable.class, Dewcatcher.Seed.class),
			recipe(GoldenJelly.class, WaterItem.class, Vegetable.class, Stormvine.Seed.class),
			recipe(Earthstar.class, WaterItem.class, Vegetable.class, Earthroot.Seed.class),
			recipe(JackOLantern.class, WaterItem.class, Vegetable.class, Firebloom.Seed.class),
			recipe(PixieParasol.class, WaterItem.class, Vegetable.class, Dreamfoil.Seed.class),
			recipe(BlueMilk.class, WaterItem.class, Vegetable.class, Sungrass.Seed.class),
			recipe(DeathCap.class, WaterItem.class, Vegetable.class, Sorrowmoss.Seed.class),
			recipe(Egg.class, Honey.class, Gel.class, StoneOre.class),
			recipe(2, Honey.class, Honeypot.class),
			recipe(2, Honey.class, Honeypot.ShatteredPot.class),
			recipe(Icecream.class, Honey.class, WaterItem.class, Icecap.Seed.class),
			recipe(Porksoup.class, MeatFood.class, WaterItem.class, Vegetable.class),
			recipe(5, Foamedbeverage.class, StoneOre.class, WaterItem.class, WaterItem.class, Plant.Seed.class, Fruit.class),
			//SPSEXPD: 水果沙拉改为「水 + 任意两个四色浆果」（黑莓/蓝莓/云莓/月亮浆果，可同种）
			new FruitSalad(),
			recipe(Vegetablekebab.class, Vegetable.class, Vegetable.class, MeatFood.class),
			recipe(HoneyWater.class, Honey.class, WaterItem.class, WaterItem.class),
			recipe(Kebab.class, Vegetable.class, MeatFood.class, MeatFood.class),
			recipe(Vegetablesoup.class, WaterItem.class, Vegetable.class, Vegetable.class),
			recipe(NutCake.class, StapleFood.class, Nut.class, Honey.class),
			recipe(MoonCake.class, StapleFood.class, Nut.class, Nut.class),
			recipe(StoneOre.class, Nut.class, Nut.class, Nut.class),
			recipe(PetFood.class, Nut.class, Nut.class, WaterItem.class),
			recipe(FoodFans.class, Nut.class, Nut.class, Potion.class),
			recipe(Frenchfries.class, Nut.class, Nut.class, Scroll.class),
			recipe(HoneyGel.class, Honey.class, Gel.class),
			recipe(Sishimi.class, WaterItem.class, MeatFood.class),
			recipe(Honeyrice.class, Honey.class, StapleFood.class),
			recipe(Honeymeat.class, Honey.class, MeatFood.class),
			recipe(Herbmeat.class, MeatFood.class, Plant.Seed.class),
			recipe(Chickennugget.class, StoneOre.class, MeatFood.class),
			recipe(Ricefood.class, StapleFood.class, WaterItem.class),
			recipe(Meatroll.class, Scroll.class, MeatFood.class),
			recipe(Vegetableroll.class, Scroll.class, Vegetable.class),
			recipe(Gel.class, StoneOre.class, WaterItem.class),
			recipe(NutVegetable.class, Nut.class),
			//SPSEXPD: 蔬菜 → 二次加工产物（把收获到的蔬菜再炼药加工）
			recipe(Adhesive.class, Durian.class),
			recipe(Capsaicin.class, Chili.class),
			recipe(TransmutePowder.class, Marigold.class),
			recipe(HealingSalve.class, HealGrass.class),
			recipe(CoolingOil.class, IceMint.class),
			recipe(Perfume.class, Tulip.class),
			recipe(ToxicExtract.class, ToxicEggplant.class),
			recipe(WakeTea.class, DreamLeaf.class),
			recipe(NutrientSolution.class, Radish.class),
			recipe(SunflowerSeed.class, Sunflower.class),
			//SPSEXPD: 无味果 + 水 → 干粮包（单材料「无味果 → 果丝」配方保持不变）
			BLANDFRUIT_TO_RATION,
			//SPSEXPD: 2 干粮包 → 3 干粮小包
			RATION_TO_SMALL,
			recipe(FruitThread.class, Blandfruit.class),
			recipe(Sedative.class, BattleFlower.class),
			recipe(RedRose.class, NutVegetable.class),
			recipe(DigestiveFluid.class, StarEaterFlower.class),
			recipe(AetherLiquid.class, TransmuteCage.class),
			recipe(CrystalShard.class, QuartzFlower.class),
			recipe(HighEnergySpore.class, DewSpore.class),
			recipe(WishPetal.class, RainbowPansy.class),
			recipe(HormoneSolution.class, Sorrel.class),
			//SPSEXPD: 火种 = 3 鲜草，或 1 枯枝（踩踏高草收获的材料）
			recipe(Tinder.class, FreshGrass.class, FreshGrass.class, FreshGrass.class),
			recipe(Tinder.class, DryTwig.class),
			//SPSEXPD: 卷轴 + 枯枝 → 挂历（翻开可看当前的游戏内日期，见 Statistics.calendarYear/Month/Day）
			recipe(WallCalendar.class, Scroll.class, DryTwig.class),
			//SPSEXPD: 许愿魔药——混乱药剂 + 4 种二次加工产物
			WISH_POTION
	);



	/**
	 * SPSEXPD: 水果沙拉——1 份水 + 任意两个「四色浆果」（黑莓 / 蓝莓 / 云莓 / 月亮浆果，可同种）。
	 * 用独立的 Recipe 实现，是因为「两个任意浆果」无法用 TypedRecipe 的按槽位类匹配表达。
	 */
	public static final class FruitSalad extends Recipe {

		private static final int TOTAL = 3;
		private static final int BERRY = 2;

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() != TOTAL) return false;
			int water = 0;
			int berry = 0;
			for (Item ingredient : ingredients) {
				if (ingredient instanceof WaterItem) {
					water++;
				} else if (isFourColorBerry(ingredient)) {
					berry++;
				} else {
					return false;
				}
			}
			return water == 1 && berry == BERRY;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			for (Item ingredient : ingredients) ingredient.quantity(ingredient.quantity() - 1);
			return sampleOutput(ingredients);
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new Fruitsalad();
		}
	}

	/** SPSEXPD: 四色浆果——踩踏高草掉落的那一池浆果（黑莓 / 蓝莓 / 云莓 / 月亮浆果）。 */
	public static boolean isFourColorBerry(Item item) {
		return item instanceof Blackberry || item instanceof Blueberry
				|| item instanceof Cloudberry || item instanceof Moonberry;
	}

	private static final Recipe GARBAGE = new Recipe() {
		@Override public boolean testIngredients(ArrayList<Item> ingredients) { return !ingredients.isEmpty(); }
		@Override public int cost(ArrayList<Item> ingredients) { return 0; }
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (ingredients.isEmpty()) return null;
			Item result = sampleOutput(ingredients);
			for (Item ingredient : ingredients) ingredient.quantity(ingredient.quantity() - 1);
			return result;
		}
		@Override public Item sampleOutput(ArrayList<Item> ingredients) { return new Garbage(ingredients.size()); }
	};

	public static Recipe garbageRecipe() {
		return GARBAGE;
	}

	/** SPSEXPD: 固定配方表（只读）——炼金指南用它生成展示条目，避免指南另抄一份。 */
	public static List<Recipe> allRecipes() {
		return RECIPES;
	}

	public static Recipe findRecipe(ArrayList<Item> ingredients) {
		for (Recipe recipe : RECIPES) {
			if (recipe.testIngredients(ingredients)) return recipe;
		}
		return null;
	}

	private SpsAlchemyRecipes() {
	}
}
