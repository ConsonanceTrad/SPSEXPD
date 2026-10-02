/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.LightDragon;
import pd.messages.InlineText;

public class LightDragonEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LightDragonEgg.class)
			.t("name", "白龙之魂")
			.t("desc", "黑暗所孕化的龙之灵魂。");
	}

	{ image = ConsumSummorDict.LIGHT_DRAGON_EGG_0; darks = 20; }
	@Override protected LegacyPet hatchling() { return new LightDragon(); }
	@Override public int value() { return 500 * quantity; }
}
