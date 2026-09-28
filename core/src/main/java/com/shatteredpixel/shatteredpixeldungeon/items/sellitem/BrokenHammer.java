package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class BrokenHammer extends SellItem {
	{ image = ItemSpriteSheet.BROKEN_HAMMER; }
	@Override public int value() { return 30 * quantity; }
}
