/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Chocobo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ChocoboEgg extends Egg {
	{ image = ItemSpriteSheet.CHOCOBO_EGG; }
	@Override protected LegacyPet hatchling() { return new Chocobo(); }
	@Override public int value() { return 500 * quantity; }
}
