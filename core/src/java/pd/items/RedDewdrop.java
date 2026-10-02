package pd.items;

import pd.atlas.items.GroundFunctionalFallingDict;


public class RedDewdrop extends ColoredDewdrop {
	{ image = GroundFunctionalFallingDict.DEWDROP_0; }
	@Override protected int baseHealing() { return 10; }
	@Override public int dewValue() { return 15 * quantity; }
}
