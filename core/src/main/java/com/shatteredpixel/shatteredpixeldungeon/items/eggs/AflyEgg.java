/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Abi;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** Alfred's whistle, represented by an egg action in the original pet system. */
public class AflyEgg extends Egg {
	{ image = ItemSpriteSheet.AFLY_EGG; }
	@Override protected LegacyPet hatchling() { return new Abi(); }
	@Override public int value() { return 500 * quantity; }
}
