/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Velocirooster;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class VelociroosterEgg extends Egg {
	{ image = ItemSpriteSheet.VELOCIROOSTER_EGG; }
	@Override protected LegacyPet hatchling() { return new Velocirooster(); }
	@Override public int value() { return 500 * quantity; }
}
