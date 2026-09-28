/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.RedDragon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RedDragonEgg extends Egg {
	{ image = ItemSpriteSheet.RED_DRAGON_EGG; burns = 20; }
	@Override protected LegacyPet hatchling() { return new RedDragon(); }
	@Override public int value() { return 500 * quantity; }
}
