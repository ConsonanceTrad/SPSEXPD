/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.items.Ankh;
import pd.items.Garbage;
import pd.items.Item;
import pd.items.armor.fusion.LifeArmor;
import pd.items.eggs.AflyEgg;
import pd.items.food.AflyFood;
import pd.items.food.ChargrilledMeat;
import pd.items.food.Food;
import pd.items.food.FrozenCarpaccio;
import pd.items.food.MeatPie;
import pd.items.food.MysteryMeat;
import pd.items.food.PhantomMeat;
import pd.items.food.SmallMeat;
import pd.items.food.StewedMeat;
import pd.items.food.fruit.Fruit;
import pd.items.food.vegetable.Vegetable;

/** Alfred's original three-ingredient cooking interface. */
public class WndAflyInfo extends WndSpsRecipe {
	public WndAflyInfo() { super(new AflyFood(), 0); }
	@Override protected int goldCost() { return 0; }
	@Override protected boolean accepts(Item item) {
		return item instanceof Food || item instanceof Ankh;
	}
	@Override protected Item mix(Item[] items) { return createResult(items); }
	public static Item createResult(Item[] items) {
		int fruit = 0, vegetable = 0, staple = 0, meat = 0, ankh = 0, aflyFood = 0;
		for (Item item : items) {
			if (item instanceof AflyFood) aflyFood++;
			else if (item instanceof Fruit) fruit++;
			else if (item instanceof Vegetable) vegetable++;
			else if (isMeat(item)) meat++;
			else if (item instanceof Food) staple++;
			else if (item instanceof Ankh) ankh++;
		}
		if (fruit == 1 && staple == 1 && meat == 1) return new AflyFood();
		if (fruit == 1 && vegetable == 2) return new LifeArmor();
		if (aflyFood == 1 && ankh == 1) return new AflyEgg();
		return new Garbage();
	}
	private static boolean isMeat(Item item) {
		return item instanceof ChargrilledMeat || item instanceof FrozenCarpaccio
				|| item instanceof MeatPie || item instanceof MysteryMeat
				|| item instanceof PhantomMeat || item instanceof SmallMeat
				|| item instanceof StewedMeat;
	}
}
