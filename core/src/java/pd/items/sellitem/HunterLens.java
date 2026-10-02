package pd.items.sellitem;

import pd.atlas.items.GroundFunctionalFallingDict;
public class HunterLens extends SellItem {
	{ image = GroundFunctionalFallingDict.DEWDROP_0; }
	@Override public int value() { return 500 * quantity; }
}
