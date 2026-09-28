package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class Simple360 extends SellItem {
	{ image = ItemSpriteSheet.SIMPLE_360; }
	@Override public int value() { return 80 * quantity; }
}
