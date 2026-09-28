/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class VIPcard extends SellItem {
	{
		image = ItemSpriteSheet.VIP_CARD;
		stackable = true;
	}
	@Override public int value() { return 400 * quantity; }
	@Override public String info() { return desc(); }
}
