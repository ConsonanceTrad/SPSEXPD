package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class Simple360 extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Simple360.class)
			.t("name", "简单发芽修改代码")
			.t("desc", "一段可以让游戏变简单的代码，其作者离开了它们所在的世界。");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 80 * quantity; }
}
