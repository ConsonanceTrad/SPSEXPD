package pd.items.specific.sellitem;

import pd.atlas.items.ConsumGoodsMaterialsGoodsDict;
public class UncleDumbbell extends SellItem {
	{ image = ConsumGoodsMaterialsGoodsDict.UNCLE_DUMBBELL; }
	@Override public int value() { return 100 * quantity; }
}
