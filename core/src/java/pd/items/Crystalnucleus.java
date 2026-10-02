/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;


public class Crystalnucleus extends Item {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; stackable = true; }
	@Override public boolean isIdentified() { return true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public int value() { return 1000 * quantity; }
}
