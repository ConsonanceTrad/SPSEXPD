/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Ricefood extends CompleteFood {
	{ image = ItemSpriteSheet.RICE_FOOD; energy = 450f; }
	@Override public int value() { return 3 * quantity; }
}
