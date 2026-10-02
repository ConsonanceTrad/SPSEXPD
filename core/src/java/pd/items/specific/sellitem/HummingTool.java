package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
public class HummingTool extends SellItem {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 120 * quantity; }
}
