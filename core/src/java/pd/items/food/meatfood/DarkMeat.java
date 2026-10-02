/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.hero.Hero;
import pd.items.food.Food;
import pd.sprites.ItemSprite;

public class DarkMeat extends MeatFood {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { DarkMeat result = new DarkMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		hero.HP = Math.min(hero.HT, hero.HP + hero.HT / 4);
	}
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
	@Override public int value() { return 3 * quantity; }
}
