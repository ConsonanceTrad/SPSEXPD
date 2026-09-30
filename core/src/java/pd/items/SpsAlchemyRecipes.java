/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.ShatteredPixelDungeon;
import pd.items.brewed.Brewed;
import pd.items.eggs.Egg;
import pd.items.food.Blandfruit;
import pd.items.food.FishCracker;
import pd.items.food.Honey;
import pd.items.food.WaterItem;
import pd.items.food.completefood.*;
import pd.items.food.fruit.Fruit;
import pd.items.food.fusion.Nut;
import pd.items.food.meatfood.MeatFood;
import pd.items.food.staplefood.OverpricedRation;
import pd.items.food.staplefood.StapleFood;
import pd.items.food.vegetable.NutVegetable;
import pd.items.food.vegetable.Truffles;
import pd.items.food.vegetable.Vegetable;
import pd.items.medicine.*;
import pd.items.potions.Potion;
import pd.items.potions.PotionOfMixing;
import pd.items.scrolls.Scroll;
import pd.plants.*;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** The deterministic recipe chain from SPS-PD 0.9.8's WndAlchemy. */
public final class SpsAlchemyRecipes {

	private static final class TypedRecipe extends Recipe {
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
	}

	private static TypedRecipe recipe(Class<? extends Item> output, Class<?>... inputs) {
		return new TypedRecipe(output, 1, inputs);
	}

	private static TypedRecipe recipe(int quantity, Class<? extends Item> output, Class<?>... inputs) {
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
			recipe(Powerpill.class, MeatFood.class, MeatFood.class, MeatFood.class, Vegetable.class),
			recipe(Hardpill.class, MeatFood.class, MeatFood.class, StoneOre.class, Vegetable.class),
			recipe(Smashpill.class, MeatFood.class, MeatFood.class, Potion.class, Vegetable.class),
			recipe(Shootpill.class, MeatFood.class, MeatFood.class, MeatFood.class, Plant.Seed.class),
			recipe(Musicpill.class, MeatFood.class, MeatFood.class, StoneOre.class, Plant.Seed.class),
			recipe(MagicPill.class, MeatFood.class, MeatFood.class, Potion.class, Plant.Seed.class),
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
			recipe(PotionOfMixing.class, Seedpod.Seed.class, Seedpod.Seed.class, Seedpod.Seed.class),
			recipe(2, Honey.class, Honeypot.class),
			recipe(2, Honey.class, Honeypot.ShatteredPot.class),
			recipe(Honey.class, Truffles.class),
			recipe(Icecream.class, Honey.class, WaterItem.class, Icecap.Seed.class),
			recipe(Porksoup.class, MeatFood.class, WaterItem.class, Vegetable.class),
			recipe(5, Foamedbeverage.class, StoneOre.class, WaterItem.class, WaterItem.class, Plant.Seed.class, Fruit.class),
			recipe(Fruitsalad.class, Fruit.class, Fruit.class, WaterItem.class),
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
			recipe(NutVegetable.class, Nut.class)
	);

	private static final Recipe SEED_TO_POTION = new Recipe() {
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			if (ingredients.size() != 3) return false;
			for (Item ingredient : ingredients) {
				if (!(ingredient instanceof Plant.Seed)
						|| !Potion.SeedToPotion.types.containsKey(ingredient.getClass())) return false;
			}
			return true;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 0;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			Item result = sampleOutput(ingredients);
			for (Item ingredient : ingredients) ingredient.quantity(ingredient.quantity() - 1);
			return result;
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			Class<? extends Potion> potion = Potion.SeedToPotion.types.get(Random.element(ingredients).getClass());
			return Reflection.newInstance(potion);
		}
	};

	private static final Recipe COOK_BREWED = new Recipe() {
		private Plant.Seed seed(ArrayList<Item> ingredients) {
			for (Item item : ingredients) if (item instanceof Plant.Seed) return (Plant.Seed) item;
			return null;
		}

		private Blandfruit fruit(ArrayList<Item> ingredients) {
			for (Item item : ingredients) if (item instanceof Blandfruit) return (Blandfruit) item;
			return null;
		}

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			Plant.Seed seed = seed(ingredients);
			Blandfruit fruit = fruit(ingredients);
			return ingredients.size() == 2 && seed != null && fruit != null
					&& fruit.potionAttrib == null && Potion.SeedToPotion.types.containsKey(seed.getClass());
		}

		@Override public int cost(ArrayList<Item> ingredients) { return 0; }

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			if (!testIngredients(ingredients)) return null;
			Item result = sampleOutput(ingredients);
			for (Item ingredient : ingredients) ingredient.quantity(ingredient.quantity() - 1);
			return result;
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return testIngredients(ingredients) ? new Brewed().cook(seed(ingredients)) : null;
		}
	};

	public static Recipe cookBrewedRecipe() {
		return COOK_BREWED;
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

	public static Recipe findRecipe(ArrayList<Item> ingredients) {
		for (Recipe recipe : RECIPES) {
			if (recipe.testIngredients(ingredients)) return recipe;
		}
		if (SEED_TO_POTION.testIngredients(ingredients)) return SEED_TO_POTION;
		if (COOK_BREWED.testIngredients(ingredients)) return COOK_BREWED;
		return null;
	}

	private SpsAlchemyRecipes() {
	}
}
