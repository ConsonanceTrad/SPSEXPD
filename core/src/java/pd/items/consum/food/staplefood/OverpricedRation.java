/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.staplefood;

import pd.atlas.items.ConsumFoodFoodDict;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class OverpricedRation extends StapleFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(OverpricedRation.class)
			.t("name", "干粮小包")
			.t("desc", "比干粮包更小的旅行速食，可以随手取用，而且比别的食物吃起来快很多（食用只需 2 回合）。");
	}



	{
		image = ConsumFoodFoodDict.SMALL_RATION_PACK;
		energy = 200f;
		hornValue = 2;
	}

	/** SPSEXPD: 干粮小包同样是速食——食用只花 2 回合。 */
	@Override protected float eatingTime() { return 2f; }

	@Override public int value() { return 3 * quantity; }
}
