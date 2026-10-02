/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;


public class VIPcard extends SellItem {
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
	}
	@Override public int value() { return 400 * quantity; }
	@Override public String info() { return desc(); }
}
