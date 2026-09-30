/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.sellitem;

import pd.items.Item;

/** Base class for the legacy collectible souvenirs whose only use is sale. */
public class SellItem extends Item {
	{ stackable = true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
