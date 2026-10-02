package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class SheepFur extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SheepFur.class)
			.t("name", "羊毛")
			.t("desc", "奇怪又普通的毛发。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 50 * quantity; }
}
