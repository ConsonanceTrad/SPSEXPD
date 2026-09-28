/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Bunny;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class EasterEgg extends Egg {
	{ image = ItemSpriteSheet.RABBIT_PET_EGG; }
	@Override protected LegacyPet hatchling() { return new Bunny(); }
	@Override public int value() { return 500 * quantity; }
}
