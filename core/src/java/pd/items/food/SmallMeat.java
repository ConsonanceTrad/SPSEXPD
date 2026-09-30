/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.food;

import pd.sprites.ItemSpriteSheet;

public class SmallMeat extends Food {

	{
		image = ItemSpriteSheet.STEAK;
		energy = 50;
		hornValue = 0;
		stackable = true;
	}

	@Override
	public int value() {
		return quantity;
	}
}
