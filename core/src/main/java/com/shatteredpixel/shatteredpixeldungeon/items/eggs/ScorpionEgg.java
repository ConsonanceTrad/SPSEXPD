/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Scorpion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ScorpionEgg extends Egg {
	{ image = ItemSpriteSheet.SCORPION_EGG; moves = 2000; }
	@Override protected LegacyPet hatchling() { return new Scorpion(); }
	@Override public int value() { return 500 * quantity; }
}
