package pd.items;

import pd.atlas.items.GroundFunctionalFallingDict;


public class YellowDewdrop extends ColoredDewdrop {
	{ image = GroundFunctionalFallingDict.DEWDROP_0; }
	@Override protected int baseHealing() { return 5; }
	@Override public int dewValue() { return 5 * quantity; }
}
