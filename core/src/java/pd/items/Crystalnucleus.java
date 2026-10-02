/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class Crystalnucleus extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Crystalnucleus.class)
			.t("name", "闪耀晶核")
			.t("desc", "这种晶核的味道并不好，但它能卖很多钱。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; stackable = true; }
	@Override public boolean isIdentified() { return true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public int value() { return 1000 * quantity; }
}
