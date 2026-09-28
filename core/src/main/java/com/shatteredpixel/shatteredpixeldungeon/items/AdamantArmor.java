package com.shatteredpixel.shatteredpixeldungeon.items;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class AdamantArmor extends Item {
	{ image = ItemSpriteSheet.ADAMANT_ARMOR; unique = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 300 * quantity; }
}
