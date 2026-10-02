package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class HummingTool extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HummingTool.class)
			.t("name", "蜂鸟开锁器")
			.t("desc", "这个东西原本能消耗钥匙打开门锁，但这个世界无法加载它所属的组模。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 120 * quantity; }
}
