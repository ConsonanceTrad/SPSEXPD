/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.BlueDragon;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;

public class BlueDragonEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BlueDragonEgg.class)
			.t("name", "蓝龙之魂")
			.t("desc", "冰霜所孕化的龙之灵魂。");
	}

	{ image = ConsumSummorDict.BLUE_DRAGON_EGG_0; freezes = 20; }
	@Override protected LegacyPet hatchling() { return new BlueDragon(); }
	@Override public int value() { return 500 * quantity; }
}
