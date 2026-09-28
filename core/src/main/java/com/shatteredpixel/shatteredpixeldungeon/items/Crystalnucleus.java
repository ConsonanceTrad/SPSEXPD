/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Crystalnucleus extends Item {
	{ image = ItemSpriteSheet.CRYSTAL_NUCLEUS; stackable = true; }
	@Override public boolean isIdentified() { return true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public int value() { return 1000 * quantity; }
}
