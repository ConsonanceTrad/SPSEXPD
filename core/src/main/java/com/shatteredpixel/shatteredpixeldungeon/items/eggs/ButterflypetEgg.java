/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.ButterflyPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ButterflypetEgg extends Egg {
	{ image = ItemSpriteSheet.BUTTERFLY_EGG; }
	@Override protected LegacyPet hatchling() { return new ButterflyPet(); }
	@Override public int value() { return 500 * quantity; }
}
