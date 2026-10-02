/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.consum.food;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class SmallMeat extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SmallMeat.class)
			.t("name", "肉干")
			.t("desc", "肉类的终极形态，丧失了肉质和肉量，但依然可以用于烹饪。");
	}


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
