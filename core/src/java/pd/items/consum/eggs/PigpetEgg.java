/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.PigPet;
import pd.messages.InlineText;
public class PigpetEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PigpetEgg.class)
			.t("name", "像素猪之魂")
			.t("desc", "召唤像素猪。");
	}



	{ image = ConsumSummorDict.PIG_PET_EGG_0; }
	@Override protected LegacyPet hatchling() { return new PigPet(); }
	@Override public int value() { return 500 * quantity; }
}
