/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PetFood extends CompleteFood {
	{
		image = ItemSpriteSheet.PET_FOOD;
		energy = 10f;
	}
	@Override public int value() { return quantity; }
}
