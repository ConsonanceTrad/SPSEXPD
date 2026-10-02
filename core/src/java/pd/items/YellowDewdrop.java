package pd.items;

import pd.atlas.items.GroundFunctionalFallingDict;
import pd.messages.InlineText;


public class YellowDewdrop extends ColoredDewdrop {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(YellowDewdrop.class)
			.t("name", "黄色露珠")
			.t("desc", "黄色的露珠。如果水袋无法继续收集，它会立即恢复少量生命。");
	}



	{ image = GroundFunctionalFallingDict.DEWDROP_0; }
	@Override protected int baseHealing() { return 5; }
	@Override public int dewValue() { return 5 * quantity; }
}
