package pd.items;

import pd.atlas.items.GroundFunctionalFallingDict;
import pd.messages.InlineText;


public class VioletDewdrop extends ColoredDewdrop {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(VioletDewdrop.class)
			.t("name", "紫色露珠")
			.t("desc", "罕见的紫色露珠。如果露珠瓶无法继续收集，它会立即恢复大量生命。");
	}



	//SPSEXPD: 原先误用普通露珠图标（DEWDROP_0），改为紫色露珠专用图标
	{ image = GroundFunctionalFallingDict.DEWDROP_3; }
	@Override protected int baseHealing() { return 50; }
	@Override public int dewValue() { return 30 * quantity; }
}
