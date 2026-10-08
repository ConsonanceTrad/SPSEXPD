/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.items.consum.food.Food;
import pd.messages.InlineText;

/** SPSEXPD: 血源风烹饪产物——鼠尾肉（烹饪技巧不足时的产物）。 */
public class TailedCutlet extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TailedCutlet.class)
			.t("name", "鼠尾肉")
			.t("desc", "剥皮后煎熟的鼠肉。至少它现在是熟的。");
	}

	{ image = ConsumFoodFoodDict.MEAT; energy = 80f; }

	@Override public int value() { return 5 * quantity; }
}
