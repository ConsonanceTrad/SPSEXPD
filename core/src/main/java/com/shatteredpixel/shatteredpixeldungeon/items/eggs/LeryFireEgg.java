/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LeryFire;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LeryFireEgg extends Egg {
	{ image = ItemSpriteSheet.LERY_FIRE_EGG; moves = 50; burns = freezes = poisons = lits = 5; }
	@Override protected LegacyPet hatchling() { return new LeryFire(); }
	@Override public int value() { return 500 * quantity; }
}
