/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.LeryFire;
import pd.messages.InlineText;

public class LeryFireEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LeryFireEgg.class)
			.t("name", "紊乱之魂")
			.t("desc", "由多种元素能量混合而成的灵魂。");
	}



	{ image = ConsumSummorDict.LERY_FIRE_EGG_0; moves = 50; burns = freezes = poisons = lits = 5; }
	@Override protected LegacyPet hatchling() { return new LeryFire(); }
	@Override public int value() { return 500 * quantity; }
}
