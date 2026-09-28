/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class OverpricedRation extends StapleFood {
	{
		image = ItemSpriteSheet.OVERPRICED;
		energy = 200f;
		hornValue = 2;
	}
	@Override public int value() { return 3 * quantity; }
}
