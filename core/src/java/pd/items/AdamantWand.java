package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;
public class AdamantWand extends Item {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; unique = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 150 * quantity; }
}
