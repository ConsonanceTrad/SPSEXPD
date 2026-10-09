/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;

/** SPSEXPD: 踩踏高草的收获——莴苣。纯食材，没有任何魔法效果。 */
public class Lettuce extends FoodVegetable {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Lettuce.class)
			.t("name", "莴苣")
			.t("desc", "在高草里随手摘到的莴苣，叶片水嫩，能填肚子。");
	}

	{ image = SpecificPlaceHolderDict.FOOD_HOLDER_0; }
}
