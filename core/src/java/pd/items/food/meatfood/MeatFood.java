/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.hero.Hero;
import pd.items.food.Food;

public class MeatFood extends Food {
	{
		stackable = true;
		image = ConsumFoodFoodDict.MEAT;
		hornValue = 1;
	}
	@Override protected void satisfy(Hero hero) {
		super.satisfy(hero);
		doEat(hero);
	}
	protected void doEat(Hero hero) { }
}
