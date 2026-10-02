/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.VioletDragon;
import pd.messages.InlineText;

public class VioletDragonEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(VioletDragonEgg.class)
			.t("name", "紫龙之魂")
			.t("desc", "大地所孕化的龙之灵魂。");
	}



	{ image = ConsumSummorDict.VIOLET_DRAGON_EGG_0; poisons = 20; }
	@Override protected LegacyPet hatchling() { return new VioletDragon(); }
	@Override public int value() { return 500 * quantity; }
}
