/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.GreenDragon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GreenDragonEgg extends Egg {
	{ image = ItemSpriteSheet.GREEN_DRAGON_EGG; lits = 20; }
	@Override protected LegacyPet hatchling() { return new GreenDragon(); }
	@Override public int value() { return 500 * quantity; }
}
