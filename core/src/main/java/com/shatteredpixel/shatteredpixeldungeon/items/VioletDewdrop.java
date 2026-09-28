package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class VioletDewdrop extends ColoredDewdrop {
	{ image = ItemSpriteSheet.VIOLET_DEWDROP; }
	@Override protected int baseHealing() { return 50; }
	@Override public int dewValue() { return 30 * quantity; }
}
