package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class SheepFur extends SellItem {
	{ image = ItemSpriteSheet.SHEEP_FUR; }
	@Override public int value() { return 50 * quantity; }
}
