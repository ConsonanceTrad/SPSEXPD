package pd.items;

import pd.atlas.items.GroundFunctionalFallingDict;
import pd.messages.InlineText;


public class RedDewdrop extends ColoredDewdrop {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RedDewdrop.class)
			.t("name", "红色露珠")
			.t("desc", "红色的露珠。如果露珠瓶无法继续收集，它会立即恢复中等量的生命。");
	}



	{ image = GroundFunctionalFallingDict.DEWDROP_0; }
	@Override protected int baseHealing() { return 10; }
	@Override public int dewValue() { return 15 * quantity; }
}
