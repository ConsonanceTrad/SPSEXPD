package pd.items.specific.sellitem;

import pd.atlas.items.GroundFunctionalFallingDict;
import pd.messages.InlineText;
public class HunterLens extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HunterLens.class)
			.t("name", "眼魔的晶状体")
			.t("desc", "可以用来制作召唤物或护目镜，但不是在这个世界。");
	}



	{ image = GroundFunctionalFallingDict.DEWDROP_0; }
	@Override public int value() { return 500 * quantity; }
}
