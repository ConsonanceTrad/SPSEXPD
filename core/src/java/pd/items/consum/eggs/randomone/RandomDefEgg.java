/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.actors.mobs.pets.Chocobo;
import pd.actors.mobs.pets.DogPet;
import pd.actors.mobs.pets.Fly;
import pd.actors.mobs.pets.Spider;
import pd.actors.mobs.pets.Stone;
import pd.messages.InlineText;

public class RandomDefEgg extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomDefEgg.class)
			.t("name", "随机防御灵魂")
			.t("desc", "召唤一个随机的基础防御宠物。");
	}

	public RandomDefEgg() { super(DogPet.class, Chocobo.class, Fly.class, Stone.class, Spider.class); }
}
