package pd.items.specific.sellitem;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.ConsumGoodsMaterialsGoodsDict;
public class BrokenHammer extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BrokenHammer.class)
			.t("name", "损坏的铁锤")
			.t("desc", "虽然已经坏了，但还能换成钱。");
	}



	{ image = ConsumGoodsMaterialsGoodsDict.WOODEN_FISH; }
	@Override public int value() { return 30 * quantity; }
}
