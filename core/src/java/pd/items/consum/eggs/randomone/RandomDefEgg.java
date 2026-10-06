/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.items.consum.eggs.ChocoboEgg;
import pd.items.consum.eggs.DogpetEgg;
import pd.items.consum.eggs.FlyEgg;
import pd.items.consum.eggs.SpiderpetEgg;
import pd.items.consum.eggs.StoneEgg;
import pd.messages.InlineText;

public class RandomDefEgg extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomDefEgg.class)
			.t("name", "随机防御灵魂")
			.t("desc", "获得一颗随机的基础防御魂石。");
	}



	public RandomDefEgg() { super(DogpetEgg.class, ChocoboEgg.class, FlyEgg.class, StoneEgg.class, SpiderpetEgg.class); }
}
