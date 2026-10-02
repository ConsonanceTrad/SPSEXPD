package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class Apk931 extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Apk931.class)
			.t("name", "蓝猫地牢测试组模")
			.t("desc", "一个测试组模。说实在的在测试中的测试是无法使用的。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 150 * quantity; }
}
