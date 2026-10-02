/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.staplefood;

import pd.atlas.items.SpecificPlaceHolderDict;


public class NormalRation extends StapleFood {
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 300f;
	}
	@Override public int value() { return 5 * quantity; }
}
