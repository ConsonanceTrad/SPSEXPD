/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.items.consum.eggs.GentleCrabEgg;
import pd.items.consum.eggs.KodoraEgg;
import pd.items.consum.eggs.RibbonRatEgg;
import pd.items.consum.eggs.SnakeEgg;
import pd.messages.InlineText;

public class RandomAtkEgg extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomAtkEgg.class)
			.t("name", "随机战斗灵魂")
			.t("desc", "获得一颗随机的战斗魂石。");
	}



	public RandomAtkEgg() { super(KodoraEgg.class, SnakeEgg.class, RibbonRatEgg.class, GentleCrabEgg.class); }
}
