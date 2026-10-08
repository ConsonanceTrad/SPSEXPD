/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.items.consum.food.Food;
import pd.messages.InlineText;

/** SPSEXPD: 血源风烹饪食材——菊苣根。 */
public class ChicoryRoot extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ChicoryRoot.class)
			.t("name", "菊苣根")
			.t("desc", "带着苦香的菊苣块茎。老练的厨师能把它烤成一道大餐。");
	}

	{
		image = ConsumFoodFoodDict.AUTHOR_NUT;
		energy = 350f;
		canBeCook = true;
	}

	@Override public int value() { return 25 * quantity; }
}
