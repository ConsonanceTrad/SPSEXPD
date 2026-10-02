package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class Mirror2 extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Mirror2.class)
			.t("name", "镜像碎片")
			.t("desc", "看上去像镜子的碎片，但是背后写着mirror2和蒸汽。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 112 * quantity; }
}
