package pd.items.sellitem;

import pd.atlas.items.ConsumGoodsMaterialsGoodsDict;
public class BottleFlower extends SellItem {
	{ image = ConsumGoodsMaterialsGoodsDict.BOTTLE_FLOWER; }
	@Override public int value() { return 1000 * quantity; }
}
