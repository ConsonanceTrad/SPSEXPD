package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class CrossPhoto extends SellItem {
	{ image = ItemSpriteSheet.CROSS_PHOTO; }
	@Override public int value() { return 150 * quantity; }
}
