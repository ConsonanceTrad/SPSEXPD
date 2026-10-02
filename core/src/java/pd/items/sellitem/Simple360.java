package pd.items.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
public class Simple360 extends SellItem {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 80 * quantity; }
}
