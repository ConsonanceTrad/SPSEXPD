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
import pd.items.consum.materials.DryGrass;
import pd.items.consum.materials.DryTwig;import pd.items.consum.materials.FreshGrass;
import pd.items.consum.materials.Tinder;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.PotionOfConfusion;
import pd.items.consum.potions.elixirs.WishPotion;
import pd.items.consum.scrolls.Scroll;
import pd.items.misc.WallCalendar;
import pd.items.quest.DarkGold;
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

	/** SPSEXPD: 力量之瓶 ↔ 法术之瓶互换（双向各耗 2 瓶炼金能量），法术之瓶由此并入主流获取途径。 */
	public static final TypedRecipe STR_TO_SPELL_BOTTLE = recipe(pd.items.SpellBottle.class,
			StrBottle.class, pd.items.consum.materials.EnergyFlask.class, pd.items.consum.materials.EnergyFlask.class);
	public static final TypedRecipe SPELL_TO_STR_BOTTLE = recipe(StrBottle.class,
			pd.items.SpellBottle.class, pd.items.consum.materials.EnergyFlask.class, pd.items.consum.materials.EnergyFlask.class);

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
			recipe(PerfectFood.class, FishCracker.class),
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
			recipe(StoneOre.class, Nut.class, Nut.class, Nut.class),
			recipe(FoodFans.class, Nut.class, Nut.class, Potion.class),
			recipe(Ricefood.class, StapleFood.class, WaterItem.class),
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
			//SPSEXPD: 火源链（docs/alchemy-rework.md §3）——鲜草 ×2 → 干草；干草 / 枯枝 → 火种
			//枯枝引火效率高：1 枯枝 → 4 火种（药水线已免火，火源预留给烹饪配方）
			recipe(DryGrass.class, FreshGrass.class, FreshGrass.class),
			recipe(4, Tinder.class, DryGrass.class),
			recipe(4, Tinder.class, DryTwig.class),
			//SPSEXPD: 取水公式 WT-01——鲜草 ×1 → 水（免火源、不失败）
			recipe(WaterItem.class, FreshGrass.class),
			//SPSEXPD: 食物线的中间产物（docs/alchemy-rework.md §7.6）
			//盐 = 原石研磨（裁决 #5，配套高草原石掉率提升）
			recipe(Salt.class, StoneOre.class),
			//面团 = 主食 ×2 + 水（与「精制米饭/米粥」的输入区分开）
			recipe(Dough.class, StapleFood.class, StapleFood.class, WaterItem.class),
			//肉馅 = 肉食 ×2 + 水（与「2 神秘之肉 → 炖肉」的输入区分开）
			recipe(Mince.class, MeatFood.class, MeatFood.class, WaterItem.class),
			//甜酱 = 蜂蜜 + 水
			recipe(Syrup.class, Honey.class, WaterItem.class),
			//腌肉 = 肉食 + 盐
			recipe(PickledMeat.class, MeatFood.class, Salt.class),
			//魔力果干 = 任意大型果实（不分类型，见 MagicDriedFruitRecipe）
			new MagicDriedFruitRecipe(),
			//SPSEXPD: 卷轴 + 枯枝 → 挂历（翻开可看当前的游戏内日期，见 Statistics.calendarYear/Month/Day）
			recipe(WallCalendar.class, Scroll.class, DryTwig.class),
			//SPSEXPD: 通用弹匣 —— 原石 + 暗金矿（枪械弹药，见 pd.items.Magazine）
			recipe(Magazine.class, StoneOre.class, DarkGold.class),
			//SPSEXPD: 许愿魔药——混乱药剂 + 4 种二次加工产物
			WISH_POTION,
			//SPSEXPD: 力量之瓶 ↔ 法术之瓶互换（双向各耗 2 瓶炼金能量）
			STR_TO_SPELL_BOTTLE,
			SPELL_TO_STR_BOTTLE
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

	/**
	 * SPSEXPD: 魔力果干——任意大型果实 ×1 烘干制成，产物**不分大果类型**。
	 * 用独立 Recipe 实现，因为「任意大型果实」是跨 20 个类的谓词，TypedRecipe 表达不了。
	 */
	public static final class MagicDriedFruitRecipe extends Recipe {

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			return ingredients.size() == 1 && isLargeFruit(ingredients.get(0));
		}

		private static boolean isLargeFruit(Item item) {
			return item instanceof Fruit && Potion.FruitToPotion.isLarge(item);
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
			return new MagicDriedFruit();
		}
	}

	//SPSEXPD: 原来的「垃圾」兜底配方（garbageRecipe）已随垃圾物品一并删除——炼金不匹配时不再有任何产物

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
