/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;
import pd.messages.InlineText;


public class Ricefood extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Ricefood.class)
			.t("name", "精制米饭")
			.t("desc", "有些时候精制米饭味道更好。\n使用_1份主食、1份水_炼金。");
	}

	{ image = ConsumFoodFoodDict.RICE_FOOD; energy = 450f; }
	@Override public int value() { return 3 * quantity; }
}
