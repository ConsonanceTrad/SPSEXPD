package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class Tissue extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Tissue.class)
			.t("name", "用餐纸巾")
			.t("desc", "义往尘沙为你准备了纸巾，有多种用途。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 120 * quantity; }
}
