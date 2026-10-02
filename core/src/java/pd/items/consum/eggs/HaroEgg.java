/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.mobs.pets.Haro;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;
public class HaroEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HaroEgg.class)
			.t("name", "待机的哈罗")
			.t("desc", "阿萨修好的不知名机械。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected LegacyPet hatchling() { return new Haro(); }
	@Override public int value() { return 500 * quantity; }
}
