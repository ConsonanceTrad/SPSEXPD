/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.BlueDragon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BlueDragonEgg extends Egg {
	{ image = ItemSpriteSheet.BLUE_DRAGON_EGG; freezes = 20; }
	@Override protected LegacyPet hatchling() { return new BlueDragon(); }
	@Override public int value() { return 500 * quantity; }
}
