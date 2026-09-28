/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.DwarfBoy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class DwarfBoyEgg extends Egg {
	{ image = ItemSpriteSheet.DWARF_BOY_EGG; }
	@Override protected LegacyPet hatchling() { return new DwarfBoy(); }
	@Override public int value() { return 500 * quantity; }
}
