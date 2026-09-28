/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LingHeart extends SellItem {
	{
		image = ItemSpriteSheet.LING_HEART;
		stackable = true;
	}
	@Override public int value() { return 100000 * quantity; }
	@Override public String info() { return desc(); }
}
