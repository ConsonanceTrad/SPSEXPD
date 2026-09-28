/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.VioletDragon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class VioletDragonEgg extends Egg {
	{ image = ItemSpriteSheet.VIOLET_DRAGON_EGG; poisons = 20; }
	@Override protected LegacyPet hatchling() { return new VioletDragon(); }
	@Override public int value() { return 500 * quantity; }
}
