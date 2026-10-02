/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.sellitem;

import pd.atlas.items.ConsumSummorDict;
import pd.messages.InlineText;
import pd.atlas.items.ConsumGoodsMaterialsGoodsDict;


public class MiniBunny extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MiniBunny.class)
			.t("name", "小兔子")
			.t("desc", "将两只兔子赶到一起就能获得一个小兔子。但这种兔子实在是太小了。");
	}



	{
		image = ConsumGoodsMaterialsGoodsDict.RABBIT_HEAD_DOLL;
		stackable = true;
	}
	@Override public int value() { return 100 * quantity; }
	@Override public String info() { return desc(); }
}
