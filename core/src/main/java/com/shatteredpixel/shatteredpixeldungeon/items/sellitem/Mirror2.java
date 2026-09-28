package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class Mirror2 extends SellItem {
	{ image = ItemSpriteSheet.MIRROR_2; }
	@Override public int value() { return 112 * quantity; }
}
