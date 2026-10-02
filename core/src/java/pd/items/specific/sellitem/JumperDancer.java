/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.sellitem;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;
import pd.atlas.items.SpecificPlaceHolderDict;


/** SPS 0.9.9 跳舞人偶：无法阻止它跳舞的收藏卖品（对照 0.9.9 JumperDancer，售价 500×数量）。 */
public class JumperDancer extends SellItem {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	//0.9.9 price() = quantity * ConsumPotionSeedBasicPotionDict.POTION_CRIMSON_0（常量值 500）
	@Override
	public int value() {
		return 500 * quantity;
	}
}
