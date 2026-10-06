/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.items.consum.eggs.ButterflypetEgg;
import pd.items.consum.eggs.DaturaEgg;
import pd.items.consum.eggs.MonkeyEgg;
import pd.items.consum.eggs.PigpetEgg;
import pd.messages.InlineText;

public class RandomColEgg extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomColEgg.class)
			.t("name", "随机资源灵魂")
			.t("desc", "获得一颗随机的资源魂石。");
	}



	public RandomColEgg() { super(ButterflypetEgg.class, MonkeyEgg.class, PigpetEgg.class, DaturaEgg.class); }
}
