/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.Datura;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;

public class DaturaEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DaturaEgg.class)
			.t("name", "曼陀罗之魂")
			.t("desc", "召唤曼陀罗。");
	}



	{ image = ConsumSummorDict.DATURA_EGG_0; }
	@Override protected LegacyPet hatchling() { return new Datura(); }
	@Override public int value() { return 500 * quantity; }
}
