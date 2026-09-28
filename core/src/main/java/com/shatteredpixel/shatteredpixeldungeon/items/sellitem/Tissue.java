package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class Tissue extends SellItem {
	{ image = ItemSpriteSheet.TISSUE; }
	@Override public int value() { return 120 * quantity; }
}
