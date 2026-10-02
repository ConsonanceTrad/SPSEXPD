package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
public class CrossPhoto extends SellItem {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 150 * quantity; }
}
