package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class BottleFlower extends SellItem {
	{ image = ItemSpriteSheet.BOTTLE_FLOWER; }
	@Override public int value() { return 1000 * quantity; }
}
