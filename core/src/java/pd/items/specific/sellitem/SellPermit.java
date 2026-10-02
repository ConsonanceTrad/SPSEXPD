package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.ConsumGoodsMaterialsGoodsDict;
public class SellPermit extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SellPermit.class)
			.t("name", "经营许可证")
			.t("desc", "经营商店的许可证的复印件，并不值钱。");
	}



	{ image = ConsumGoodsMaterialsGoodsDict.BROKEN_CROSSBOW; }
	@Override public int value() { return 50 * quantity; }
}
