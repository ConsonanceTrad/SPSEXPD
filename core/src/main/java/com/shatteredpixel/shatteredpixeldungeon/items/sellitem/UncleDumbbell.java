package com.shatteredpixel.shatteredpixeldungeon.items.sellitem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class UncleDumbbell extends SellItem {
	{ image = ItemSpriteSheet.UNCLE_DUMBBELL; }
	@Override public int value() { return 100 * quantity; }
}
