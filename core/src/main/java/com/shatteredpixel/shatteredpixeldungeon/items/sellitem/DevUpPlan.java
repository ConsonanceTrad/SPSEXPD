/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** The unreadable development plan carried by the SPS author NPC. */
public class DevUpPlan extends SellItem {

	{
		image = ItemSpriteSheet.GUIDE_PAGE;
		stackable = true;
	}

	@Override public int value() { return 500 * quantity; }
}
