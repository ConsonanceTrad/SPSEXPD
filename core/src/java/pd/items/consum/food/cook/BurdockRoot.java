/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.items.consum.food.Food;
import pd.messages.InlineText;

/** SPSEXPD: 血源风烹饪食材——牛蒡根。 */
public class BurdockRoot extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BurdockRoot.class)
			.t("name", "牛蒡根")
			.t("desc", "沾着泥土的牛蒡根。洗净煎熟后是一道不错的野味。");
	}

	{
		image = ConsumFoodFoodDict.RAW_NUT;
		energy = 50f;
		canBeCook = true;
	}

	@Override public int value() { return 1 * quantity; }
}
