/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.DogPet;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;

public class DogpetEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DogpetEgg.class)
			.t("name", "忠犬之魂")
			.t("desc", "召唤忠犬。");
	}



	{ image = ConsumSummorDict.DOG_PET_EGG_0; }
	@Override protected LegacyPet hatchling() { return new DogPet(); }
	@Override public int value() { return 500 * quantity; }
}
