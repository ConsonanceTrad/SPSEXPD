package pd.items.specific.sellitem;

import pd.atlas.items.ConsumGoodsMaterialsGoodsDict;
import pd.messages.InlineText;
public class BottleFlower extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BottleFlower.class)
			.t("name", "试管玫瑰")
			.t("desc", "冰杖最喜欢的物品之一。");
	}

	{ image = ConsumGoodsMaterialsGoodsDict.BOTTLE_FLOWER; }
	@Override public int value() { return 1000 * quantity; }
}
