/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.staplefood;

import pd.atlas.items.ConsumFoodFoodDict;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;


public class NormalRation extends StapleFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NormalRation.class)
			.t("name", "干粮包")
			.t("desc", "为旅行所制作的快速食品，可以随手取用，而且比别的食物吃起来快很多（食用只需 2 回合）。");
	}



	{
		image = ConsumFoodFoodDict.RATION_PACK;
		//SPSEXPD: 饱食度 300 -> 400——号角要 6 点充能才凝出一包，干粮包该更实在
		energy = 400f;
	}

	/** SPSEXPD: 干粮是速食——食用只花 2 回合（覆盖 Food 的通用进食时间）。 */
	@Override protected float eatingTime() { return 2f; }

	@Override public int value() { return 5 * quantity; }
}
