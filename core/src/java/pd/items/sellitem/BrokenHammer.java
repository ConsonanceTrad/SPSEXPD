package pd.items.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
public class BrokenHammer extends SellItem {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 30 * quantity; }
}
