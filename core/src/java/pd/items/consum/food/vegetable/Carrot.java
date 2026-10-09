/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;

/** SPSEXPD: 踩踏高草的收获——胡萝卜。纯食材，没有任何魔法效果。 */
public class Carrot extends Vegetable {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Carrot.class)
			.t("name", "胡萝卜")
			.t("desc", "踩踏高草时顺手拔出来的野胡萝卜，脆生生的，能填肚子。");
	}

	{ image = SpecificPlaceHolderDict.FOOD_HOLDER_0; }
}
