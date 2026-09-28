/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Gel extends CompleteFood {
	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0000FF);
	{ image = ItemSpriteSheet.GEL; energy = 10f; }
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
	@Override public int value() { return 50 * quantity; }
}
