package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class HunterLens extends SellItem {
	{ image = ItemSpriteSheet.DEWDROP; }
	@Override public int value() { return 500 * quantity; }
}
