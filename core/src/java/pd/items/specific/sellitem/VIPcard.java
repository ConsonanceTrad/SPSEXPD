/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class VIPcard extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(VIPcard.class)
			.t("name", "VIP卡")
			.t("desc", "来自另一个时空的卡片，上面印着一位导师的名字。");
	}

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
	}
	@Override public int value() { return 400 * quantity; }
	@Override public String info() { return desc(); }
}
