/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.items.consum.food.Food;
import pd.messages.InlineText;

/**
 * SPSEXPD: 血源风烹饪食材（移植自血源诅咒地牢 1.8.2 的简单烹饪体系）。
 * canBeCook 食材可用平底煎锅烹制，产物取决于烹饪技巧。
 */
public class WildCarrot extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WildCarrot.class)
			.t("name", "野胡萝卜")
			.t("desc", "地里挖出来的野生胡萝卜。生吃没什么滋味，但用平底煎锅烹制后会好吃得多。");
	}

	{
		image = ConsumFoodFoodDict.BLANDFRUIT;
		energy = 110f;
		canBeCook = true;
	}

	@Override public int value() { return 1 * quantity; }
}
