/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import pd.items.consum.food.SmallMeat;
import pd.sprites.ItemSprite;

public class LightMeat extends MeatFood {
	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { LightMeat result = new LightMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		if (Dungeon.level != null) Dungeon.level.drop(new SmallMeat(), hero.pos);
	}
	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
	@Override public int value() { return 3 * quantity; }
}
