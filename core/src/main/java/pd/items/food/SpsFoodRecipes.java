/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food;

import pd.ShatteredPixelDungeon;
import pd.items.Honeypot;
import pd.items.Item;
import pd.items.Recipe;
import pd.items.food.completefood.NutCake;
import pd.items.food.completefood.PetFood;
import pd.items.food.completefood.Sishimi;
import pd.items.food.completefood.ZongZi;
import pd.items.food.fusion.Nut;
import pd.items.food.meatfood.MeatFood;
import pd.items.food.staplefood.StapleFood;
import pd.items.food.vegetable.Truffles;
import pd.items.food.vegetable.Vegetable;
import render.utils.Reflection;

import java.util.ArrayList;

/** SPS-PD recipes that fit Shattered's current three-slot alchemy interface. */
public final class SpsFoodRecipes {

	private abstract static class TypedRecipe extends Recipe {
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
			if (ingredients.size() != inputs.length) return false;
			for (Item ingredient : ingredients) {
				if (!ingredient.isIdentified() || ingredient.quantity() < 1) return false;
			}
			return matches(ingredients, 0, new boolean[ingredients.size()]);
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
			for (Item ingredient : ingredients) ingredient.quantity(ingredient.quantity() - 1);
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

	public static final class MakeZongZi extends TypedRecipe {
		public MakeZongZi() { super(ZongZi.class, 1, StapleFood.class, Vegetable.class, MeatFood.class); }
	}

	public static final class MakeNutCake extends TypedRecipe {
		public MakeNutCake() { super(NutCake.class, 1, StapleFood.class, Nut.class, Honey.class); }
	}

	public static final class MakePetFood extends TypedRecipe {
		public MakePetFood() { super(PetFood.class, 1, Nut.class, Nut.class, WaterItem.class); }
	}

	public static final class MakeSishimi extends TypedRecipe {
		public MakeSishimi() { super(Sishimi.class, 1, WaterItem.class, MeatFood.class); }
	}

	public static final class ExtractHoneypot extends TypedRecipe {
		public ExtractHoneypot() { super(Honey.class, 2, Honeypot.class); }
	}

	public static final class ExtractShatteredPot extends TypedRecipe {
		public ExtractShatteredPot() { super(Honey.class, 2, Honeypot.ShatteredPot.class); }
	}

	public static final class ExtractTruffles extends TypedRecipe {
		public ExtractTruffles() { super(Honey.class, 1, Truffles.class); }
	}

	private SpsFoodRecipes() { }
}
