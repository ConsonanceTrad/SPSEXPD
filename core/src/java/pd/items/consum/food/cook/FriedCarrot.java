/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.items.consum.food.Food;
import pd.messages.InlineText;

/** SPSEXPD: 血源风烹饪产物——炸胡萝卜（烹饪技巧不足时的产物）。 */
public class FriedCarrot extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FriedCarrot.class)
			.t("name", "炸胡萝卜")
			.t("desc", "煎得软塌塌的胡萝卜。也许有点黄油会更好。");
	}

	{ image = ConsumFoodFoodDict.FRENCH_FRIES; energy = 220f; }

	@Override public int value() { return 5 * quantity; }
}
