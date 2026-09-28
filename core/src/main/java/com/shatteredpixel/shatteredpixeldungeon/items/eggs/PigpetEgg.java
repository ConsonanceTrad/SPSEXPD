/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.PigPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class PigpetEgg extends Egg {
	{ image = ItemSpriteSheet.PIG_PET_EGG; }
	@Override protected LegacyPet hatchling() { return new PigPet(); }
	@Override public int value() { return 500 * quantity; }
}
