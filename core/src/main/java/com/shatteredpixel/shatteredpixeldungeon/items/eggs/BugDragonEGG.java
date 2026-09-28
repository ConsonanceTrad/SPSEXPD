/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.BugDragon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** Retains the unusual class spelling used by SPS-PD 0.9.8. */
public class BugDragonEGG extends Egg {
	{
		image = ItemSpriteSheet.BUG_DRAGON_EGG;
		moves = 2000; burns = freezes = poisons = lits = darks = lights = 20;
	}
	@Override protected LegacyPet hatchling() { return new BugDragon(); }
	@Override public int value() { return 500 * quantity; }
}
