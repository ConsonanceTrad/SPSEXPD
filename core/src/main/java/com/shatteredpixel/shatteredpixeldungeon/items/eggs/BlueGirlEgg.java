/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.BlueGirl;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BlueGirlEgg extends Egg {
	{ image = ItemSpriteSheet.BLUE_GIRL_EGG; poisons = 30; lights = 66; }
	@Override protected LegacyPet hatchling() { return new BlueGirl(); }
	@Override public int value() { return 500 * quantity; }
}
