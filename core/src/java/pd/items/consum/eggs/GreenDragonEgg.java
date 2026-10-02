/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.GreenDragon;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;

public class GreenDragonEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GreenDragonEgg.class)
			.t("name", "绿龙之魂")
			.t("desc", "雷电所孕化的龙之灵魂。");
	}

	{ image = ConsumSummorDict.GREEN_DRAGON_EGG_0; lits = 20; }
	@Override protected LegacyPet hatchling() { return new GreenDragon(); }
	@Override public int value() { return 500 * quantity; }
}
