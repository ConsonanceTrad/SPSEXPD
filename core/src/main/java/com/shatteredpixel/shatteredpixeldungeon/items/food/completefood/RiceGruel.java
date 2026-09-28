/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RiceGruel extends CompleteFood {

	{
		image = ItemSpriteSheet.RICE_GRUEL;
		energy = 250f;
	}

	public RiceGruel() { this(2); }
	public RiceGruel(int number) { quantity = number; }

	@Override public int value() { return 10 * quantity; }
}
