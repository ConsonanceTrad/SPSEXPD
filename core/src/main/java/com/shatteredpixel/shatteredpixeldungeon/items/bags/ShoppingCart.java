/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.brewed.Brewed;
import com.shatteredpixel.shatteredpixeldungeon.items.food.BugMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

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
