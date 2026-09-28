package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RedDewdrop extends ColoredDewdrop {
	{ image = ItemSpriteSheet.RED_DEWDROP; }
	@Override protected int baseHealing() { return 10; }
	@Override public int dewValue() { return 15 * quantity; }
}
