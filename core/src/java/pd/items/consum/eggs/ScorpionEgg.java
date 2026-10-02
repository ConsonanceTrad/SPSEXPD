/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.Scorpion;
import pd.messages.InlineText;

public class ScorpionEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScorpionEgg.class)
			.t("name", "毒蛰之魂")
			.t("desc", "召唤一只巨大的血蝎。");
	}



	{ image = ConsumSummorDict.SCORPION_EGG_0; moves = 2000; }
	@Override protected LegacyPet hatchling() { return new Scorpion(); }
	@Override public int value() { return 500 * quantity; }
}
