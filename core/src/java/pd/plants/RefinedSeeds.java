/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.plants;

import pd.atlas.items.ConsumPotionSeedSeedDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.Item;
import pd.items.Recipe;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.equipment.weapon.missiles.arrows.BlindFruit;
import pd.items.equipment.weapon.missiles.arrows.CharmFruit;
import pd.items.equipment.weapon.missiles.arrows.DewFruit;
import pd.items.equipment.weapon.missiles.arrows.FireFruit;
import pd.items.equipment.weapon.missiles.arrows.FlavorlessFruit;
import pd.items.equipment.weapon.missiles.arrows.FreshFruit;
import pd.items.equipment.weapon.missiles.arrows.GlassFruit;
import pd.items.equipment.weapon.missiles.arrows.HealFruit;
import pd.items.equipment.weapon.missiles.arrows.IceFruit;
import pd.items.equipment.weapon.missiles.arrows.NutFruit;
import pd.items.equipment.weapon.missiles.arrows.RootFruit;
import pd.items.equipment.weapon.missiles.arrows.RotFruit;
import pd.items.equipment.weapon.missiles.arrows.SeedFruit;
import pd.items.equipment.weapon.missiles.arrows.ShockFruit;
import pd.items.equipment.weapon.missiles.arrows.SmokeFruit;
import pd.items.equipment.weapon.missiles.arrows.StarEaterFruit;
import pd.items.equipment.weapon.missiles.arrows.StarFruit;
import pd.items.equipment.weapon.missiles.arrows.SwiftFruit;
import pd.items.equipment.weapon.missiles.arrows.ToxicFruit;
import pd.items.equipment.weapon.missiles.arrows.TransmuteFruit;
import pd.levels.Level;
import pd.messages.Messages;
import pd.windows.WndBag;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * SPSEXPD: 精制种子——用「对应作物的果实（普通或大型）+ 任意一种蔬菜」在炼金釜直接炼成。
 *
 * 种在普通地板上会长成一株精心作物（2~3 枚果实 + 2~3 个蔬菜，与花盆精心种植同档），
 * 但需要 {@link #GROW_TURNS} 回合才能完全长成；长成之前被踩踏只会化作“被踩踏的高草”。
 * 每种作物各有一粒精制种子，共 20 种。
 */
public final class RefinedSeeds {

	private RefinedSeeds() { }

	/** SPSEXPD: 精制种子作物完全长成所需的回合数。 */
	public static final int GROW_TURNS = 50;

	/** 精准种子基类：长成对应植物的精心（果丛）形态，并带成长计时。 */
	public abstract static class RefinedSeed extends Plant.Seed {

		/** SPSEXPD: 精制种子也接受催熟以外的所有正常种植方式（含花盆）。 */
		@Override
		public String name() {
			return "精制" + Messages.get(plantClass, "name") + "种子";
		}

		@Override
		public String desc() {
			return "用果实与蔬菜炼成的精制种子。种在普通地板上能长成一株精心作物，"
					+ "但要经过 " + GROW_TURNS + " 回合才能完全长成；长成之前被踩踏，"
					+ "它只会化作被踩踏的高草。";
		}

		//SPSEXPD: 精制种子不论种在哪里都长成“精心作物”（果丛形态 + 成熟计时）
		@Override
		public Plant couch(int pos, Level level) {
			return refine(super.excouch(pos, level));
		}

		@Override
		public Plant excouch(int pos, Level level) {
			return refine(super.excouch(pos, level));
		}

		private static Plant refine(Plant plant) {
			if (plant instanceof SpsFruitBush) {
				((SpsFruitBush) plant).potGrown = true;
			}
			plant.growTurns = GROW_TURNS;
			return plant;
		}
	}

	public static class FirebloomRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_FIREBLOOM; plantClass = Firebloom.class; explantClass = Firebloom.ExFirebloom.class; }
	}

	public static class IcecapRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_ICECAP; plantClass = Icecap.class; explantClass = Icecap.ExIcecap.class; }
	}

	public static class SorrowmossRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_SORROWMOSS_0; plantClass = Sorrowmoss.class; explantClass = Sorrowmoss.ExSorrowmoss.class; }
	}

	public static class BlindweedRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_BLINDWEED_0; plantClass = Blindweed.class; explantClass = Blindweed.ExBlindweed.class; }
	}

	public static class SungrassRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_SUNGRASS; plantClass = Sungrass.class; explantClass = Sungrass.ExSungrass.class; }
	}

	public static class EarthrootRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_EARTHROOT_0; plantClass = Earthroot.class; explantClass = Earthroot.ExEarthroot.class; }
	}

	public static class FadeleafRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_FADELEAF_0; plantClass = Fadeleaf.class; explantClass = Fadeleaf.ExFadeleaf.class; }
	}

	public static class RotberryRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_ROT_BERRY; plantClass = Rotberry.class; explantClass = Rotberry.ExRotberry.class; }
	}

	public static class BlandfruitRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_BLANDFRUIT; plantClass = BlandfruitBush.class; explantClass = BlandfruitBush.ExBlandfruitBush.class; }
	}

	public static class DreamfoilRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_MAGEROYAL_0; plantClass = Dreamfoil.class; explantClass = Dreamfoil.ExDreamfoil.class; }
	}

	public static class StormvineRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_STORMVINE; plantClass = Stormvine.class; explantClass = Stormvine.ExStormvine.class; }
	}

	public static class NutPlantRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_NUTVINE; plantClass = NutPlant.class; explantClass = NutPlant.ExNutPlant.class; }
	}

	public static class StarflowerRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_STARFLOWER_0; plantClass = Starflower.class; explantClass = Starflower.ExStarflower.class; }
	}

	public static class ReNepenthRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_TRANSMUTE_CAGE; plantClass = ReNepenth.class; explantClass = ReNepenth.ExReNepenth.class; }
	}

	public static class StarEaterRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_STAREATER; plantClass = StarEater.class; explantClass = StarEater.ExStarEater.class; }
	}

	public static class DewcatcherRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_DEWCATCHER; plantClass = Dewcatcher.class; explantClass = Dewcatcher.ExDewcatcher.class; }
	}

	public static class SeedpodRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_POD; plantClass = Seedpod.class; explantClass = Seedpod.ExSeedpod.class; }
	}

	public static class FreshberryRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_ROT_BERRY; plantClass = Freshberry.class; explantClass = Freshberry.ExFreshberry.class; }
	}

	public static class SiOtwoFlowerRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_QUARTZFLOWER; plantClass = SiOtwoFlower.class; explantClass = SiOtwoFlower.ExSiOtwoFlower.class; }
	}

	public static class SwiftthistleRefined extends RefinedSeed {
		{ image = ConsumPotionSeedSeedDict.SEED_SWIFTTHISTLE; plantClass = Swiftthistle.class; explantClass = Swiftthistle.ExSwiftthistle.class; }
	}

	/** 果实类 → 该作物的精制种子类（大型果实沿继承链匹配）。 */
	private static final LinkedHashMap<Class<?>, Class<? extends Plant.Seed>> BY_FRUIT = new LinkedHashMap<>();

	static {
		BY_FRUIT.put(FireFruit.class,       FirebloomRefined.class);
		BY_FRUIT.put(IceFruit.class,        IcecapRefined.class);
		BY_FRUIT.put(ToxicFruit.class,      SorrowmossRefined.class);
		BY_FRUIT.put(BlindFruit.class,      BlindweedRefined.class);
		BY_FRUIT.put(HealFruit.class,       SungrassRefined.class);
		BY_FRUIT.put(RootFruit.class,       EarthrootRefined.class);
		BY_FRUIT.put(SmokeFruit.class,      FadeleafRefined.class);
		BY_FRUIT.put(RotFruit.class,        RotberryRefined.class);
		BY_FRUIT.put(FlavorlessFruit.class, BlandfruitRefined.class);
		BY_FRUIT.put(CharmFruit.class,      DreamfoilRefined.class);
		BY_FRUIT.put(ShockFruit.class,      StormvineRefined.class);
		BY_FRUIT.put(NutFruit.class,        NutPlantRefined.class);
		BY_FRUIT.put(StarFruit.class,       StarflowerRefined.class);
		BY_FRUIT.put(TransmuteFruit.class,  ReNepenthRefined.class);
		BY_FRUIT.put(StarEaterFruit.class,  StarEaterRefined.class);
		BY_FRUIT.put(DewFruit.class,        DewcatcherRefined.class);
		BY_FRUIT.put(SeedFruit.class,       SeedpodRefined.class);
		BY_FRUIT.put(FreshFruit.class,      FreshberryRefined.class);
		BY_FRUIT.put(GlassFruit.class,      SiOtwoFlowerRefined.class);
		BY_FRUIT.put(SwiftFruit.class,      SwiftthistleRefined.class);
	}

	/** 沿继承链查找果实对应的精制种子（大型果实是其普通果实的子类）。 */
	public static Class<? extends Plant.Seed> seedClassFor(Item fruit) {
		if (fruit == null) return null;
		for (Class<?> type = fruit.getClass(); type != null; type = type.getSuperclass()) {
			Class<? extends Plant.Seed> seed = BY_FRUIT.get(type);
			if (seed != null) return seed;
		}
		return null;
	}

	/** 自身不在映射表中、却能沿父类匹配到精制种子，即为大型果实。 */
	public static boolean isLarge(Item fruit) {
		return seedClassFor(fruit) != null && !BY_FRUIT.containsKey(fruit.getClass());
	}

	/**
	 * SPSEXPD: 精制种子配方——投入「对应作物的果实（普通或大型）+ 任意一种蔬菜」。
	 * 普通果实炼出 1 粒精制种子，大型果实炼出 2 粒。
	 */
	public static class RefinedSeedRecipe extends Recipe {

		public static final int COUNT = 2;

		static {
			pd.messages.InlineText.of(RefinedSeedRecipe.class)
				.t("name", "精制种子");
		}

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() != COUNT) return false;
			int fruits = 0, vegetables = 0;
			for (Item ingredient : ingredients) {
				if (seedClassFor(ingredient) != null) fruits++;
				else if (ingredient instanceof Vegetable) vegetables++;
			}
			return fruits == 1 && vegetables == 1;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;

			Item fruit = null;
			for (Item ingredient : ingredients) {
				if (seedClassFor(ingredient) != null) fruit = ingredient;
			}
			for (Item ingredient : ingredients) {
				ingredient.quantity(ingredient.quantity() - 1);
			}

			Class<? extends Plant.Seed> seedClass = seedClassFor(fruit);
			if (seedClass == null) return null;
			Plant.Seed seed = Reflection.newInstance(seedClass);
			//SPSEXPD: 大型果实能多炼出一粒精制种子
			seed.quantity(isLarge(fruit) ? 2 : 1);
			return seed;
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new WndBag.Placeholder(SpecificPlaceHolderDict.SEED_HOLDER_0) {

				@Override
				public String name() {
					return Messages.get(RefinedSeedRecipe.class, "name");
				}

				@Override
				public String info() {
					return "";
				}
			};
		}
	}
}
