/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.DogPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class DogpetEgg extends Egg {
	{ image = ItemSpriteSheet.DOG_PET_EGG; }
	@Override protected LegacyPet hatchling() { return new DogPet(); }
	@Override public int value() { return 500 * quantity; }
}
