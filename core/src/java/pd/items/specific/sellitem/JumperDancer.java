/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.sellitem;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


/** SPS 0.9.9 跳舞人偶：无法阻止它跳舞的收藏卖品（对照 0.9.9 JumperDancer，售价 500×数量）。 */
public class JumperDancer extends SellItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(JumperDancer.class)
			.t("name", "跳舞人偶")
			.t("desc", "你无法阻止这个人偶跳舞。");
	}




	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	//0.9.9 price() = quantity * ConsumPotionSeedBasicPotionDict.POTION_CRIMSON_0（常量值 500）
	@Override
	public int value() {
		return 500 * quantity;
	}
}
