/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.staplefood;

import pd.atlas.items.SpecificPlaceHolderDict;


public class OverpricedRation extends StapleFood {
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 200f;
		hornValue = 2;
	}
	@Override public int value() { return 3 * quantity; }
}
