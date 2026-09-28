package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class NouthSouth extends SellItem {
	{ image = ItemSpriteSheet.NOUTH_SOUTH; }
	@Override public int value() { return 500 * quantity; }
}
