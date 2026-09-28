/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LightDragon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LightDragonEgg extends Egg {
	{ image = ItemSpriteSheet.LIGHT_DRAGON_EGG; darks = 20; }
	@Override protected LegacyPet hatchling() { return new LightDragon(); }
	@Override public int value() { return 500 * quantity; }
}
