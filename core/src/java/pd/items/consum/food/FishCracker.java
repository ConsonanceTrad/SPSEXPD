/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food;

import pd.atlas.items.SpecificPlaceHolderDict;


public class FishCracker extends Food {
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 200f;
		hornValue = 0;
	}
	@Override public int value() { return 1000 * quantity; }
}
