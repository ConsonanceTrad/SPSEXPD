package pd.items;

import pd.atlas.items.GroundFunctionalFallingDict;


public class VioletDewdrop extends ColoredDewdrop {
	{ image = GroundFunctionalFallingDict.DEWDROP_0; }
	@Override protected int baseHealing() { return 50; }
	@Override public int dewValue() { return 30 * quantity; }
}
