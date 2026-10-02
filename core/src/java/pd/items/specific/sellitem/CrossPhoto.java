package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
public class CrossPhoto extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CrossPhoto.class)
			.t("name", "拼凑相片")
			.t("desc", "一张由正常照片、照片底片和曝光底片的碎片拼凑的照片。照片上一家三口的表情显得十分怪诞。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public int value() { return 150 * quantity; }
}
