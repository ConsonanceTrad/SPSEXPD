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
			.t("desc", "容量比干粮包更小，很受年轻冒险者欢迎。");
	}



	{
		image = ConsumFoodFoodDict.SMALL_RATION_PACK;
		energy = 200f;
		hornValue = 2;
	}
	@Override public int value() { return 3 * quantity; }
}
