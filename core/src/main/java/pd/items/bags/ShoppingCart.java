/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.bags;

import pd.items.Item;
import pd.items.brewed.Brewed;
import pd.items.food.BugMeat;
import pd.items.food.Food;
import pd.items.potions.brews.Brew;
import pd.sprites.ItemSpriteSheet;

/** The thirty-slot SPS food and brew container. */
public class ShoppingCart extends Bag {

	{
		image = ItemSpriteSheet.SHOPPING_CART;
	}

	@Override
	public boolean canHold(Item item) {
		if ((item instanceof Food || item instanceof Brew || item instanceof Brewed) && !(item instanceof BugMeat)) {
			return super.canHold(item);
		}
		return false;
	}

	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
