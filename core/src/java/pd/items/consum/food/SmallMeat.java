/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.consum.food;

import pd.atlas.items.SpecificPlaceHolderDict;


public class SmallMeat extends Food {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 50;
		hornValue = 0;
		stackable = true;
	}

	@Override
	public int value() {
		return quantity;
	}
}
