/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Haro;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class HaroEgg extends Egg {
	{ image = ItemSpriteSheet.HARO_EGG; }
	@Override protected LegacyPet hatchling() { return new Haro(); }
	@Override public int value() { return 500 * quantity; }
}
