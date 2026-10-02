package pd.items.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
public class SheepFur extends SellItem {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 50 * quantity; }
}
