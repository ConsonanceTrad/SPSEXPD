/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.mobs.pets.ButterflyPet;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;
import pd.atlas.items.ConsumSummorDict;

public class ButterflypetEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ButterflypetEgg.class)
			.t("name", "萤石粉蝶之魂")
			.t("desc", "召唤萤石粉蝶。");
	}



	{ image = ConsumSummorDict.AFLY_EGG_0; }
	@Override protected LegacyPet hatchling() { return new ButterflyPet(); }
	@Override public int value() { return 500 * quantity; }
}
