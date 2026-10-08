/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.items.consum.food.Food;
import pd.messages.InlineText;

/** SPSEXPD: 血源风烹饪产物——炸菊苣（烹饪技巧不足时的产物）。 */
public class FriedChicoryRoot extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FriedChicoryRoot.class)
			.t("name", "炸菊苣")
			.t("desc", "洗净煎熟的菊苣块茎，带着一点熟悉的苦香。");
	}

	{ image = ConsumFoodFoodDict.PINK_BRICK; energy = 350f; }

	@Override public int value() { return 30 * quantity; }
}
