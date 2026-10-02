/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.YearPet;
import pd.messages.InlineText;

/** The guaranteed soul dropped by the Spring Festival year beast. */
public class YearPetEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(YearPetEgg.class)
			.t("name", "年兽之魂")
			.t("desc", "年兽宝宝的灵魂。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override protected LegacyPet hatchling() { return new YearPet(); }
	@Override public int value() { return 500 * quantity; }
}
