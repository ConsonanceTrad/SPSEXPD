/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Garbage;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.fusion.LifeArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.AflyEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.food.AflyFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.PhantomMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Fruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.Vegetable;

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
