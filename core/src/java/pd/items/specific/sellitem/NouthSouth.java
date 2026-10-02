package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class NouthSouth extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NouthSouth.class)
			.t("name", "南北手柄")
			.t("desc", "矮人所使用的手柄，用于尝试体感类的掌机游戏。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 500 * quantity; }
}
