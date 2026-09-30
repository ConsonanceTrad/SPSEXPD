/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.meatfood;

import pd.items.food.Food;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;

public class FireMeat extends MeatFood {
	private static final ItemSprite.Glowing RED = new ItemSprite.Glowing(0xCC0000);
	{
		image = ItemSpriteSheet.MEAT;
		energy = 150f;
	}
	public static Food cook(int quantity) { FireMeat result = new FireMeat(); result.quantity(quantity); return result; }
	@Override public ItemSprite.Glowing glowing() { return RED; }
	@Override public int value() { return 2 * quantity; }
}
