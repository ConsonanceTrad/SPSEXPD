package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class SellPermit extends SellItem {
	{ image = ItemSpriteSheet.SELL_PERMIT; }
	@Override public int value() { return 50 * quantity; }
}
