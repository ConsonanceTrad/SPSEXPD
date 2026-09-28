/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Datura;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class DaturaEgg extends Egg {
	{ image = ItemSpriteSheet.DATURA_EGG; }
	@Override protected LegacyPet hatchling() { return new Datura(); }
	@Override public int value() { return 500 * quantity; }
}
