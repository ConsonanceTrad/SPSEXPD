package pd.items.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
public class NouthSouth extends SellItem {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 500 * quantity; }
}
