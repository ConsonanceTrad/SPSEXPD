/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.actors.mobs.pets.ButterflyPet;
import pd.actors.mobs.pets.Datura;
import pd.actors.mobs.pets.Monkey;
import pd.actors.mobs.pets.PigPet;
import pd.messages.InlineText;

public class RandomColEgg extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomColEgg.class)
			.t("name", "随机资源灵魂")
			.t("desc", "召唤一个随机的资源宠物。");
	}



	public RandomColEgg() { super(ButterflyPet.class, Monkey.class, PigPet.class, Datura.class); }
}
