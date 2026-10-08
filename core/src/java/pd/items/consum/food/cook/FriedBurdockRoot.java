/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.items.consum.food.Food;
import pd.messages.InlineText;

/** SPSEXPD: 血源风烹饪产物——炸牛蒡（烹饪技巧不足时的产物）。 */
public class FriedBurdockRoot extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FriedBurdockRoot.class)
			.t("name", "炸牛蒡")
			.t("desc", "洗净煎熟的牛蒡根，味道朴实。");
	}

	{ image = ConsumFoodFoodDict.CHOCOLATE; energy = 100f; }

	@Override public int value() { return 5 * quantity; }
}
