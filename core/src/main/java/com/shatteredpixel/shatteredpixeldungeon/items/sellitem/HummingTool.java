package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class HummingTool extends SellItem {
	{ image = ItemSpriteSheet.HUMMING_TOOL; }
	@Override public int value() { return 120 * quantity; }
}
