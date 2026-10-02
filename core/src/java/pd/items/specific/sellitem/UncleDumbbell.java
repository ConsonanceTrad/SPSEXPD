package pd.items.specific.sellitem;

import pd.atlas.items.ConsumGoodsMaterialsGoodsDict;
import pd.messages.InlineText;
public class UncleDumbbell extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(UncleDumbbell.class)
			.t("name", "发芽的哑铃")
			.t("desc", "长期未用的哑铃，任由杂草在上面发芽。");
	}

	{ image = ConsumGoodsMaterialsGoodsDict.UNCLE_DUMBBELL; }
	@Override public int value() { return 100 * quantity; }
}
