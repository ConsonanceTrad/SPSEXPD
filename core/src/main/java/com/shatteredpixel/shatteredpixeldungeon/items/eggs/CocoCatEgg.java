/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.CocoCat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.LegacyPet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class CocoCatEgg extends Egg {
	{ image = ItemSpriteSheet.COCO_CAT_EGG; }
	@Override protected LegacyPet hatchling() { return new CocoCat(); }
	@Override public int value() { return 500 * quantity; }
}
