/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.sellitem;

import pd.atlas.items.ConsumSummorDict;


public class MiniBunny extends SellItem {
	{
		image = ConsumSummorDict.RABBIT_PET_EGG_0;
		stackable = true;
	}
	@Override public int value() { return 100 * quantity; }
	@Override public String info() { return desc(); }
}
