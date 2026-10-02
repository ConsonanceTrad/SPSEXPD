/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.Chocobo;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;

public class ChocoboEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ChocoboEgg.class)
			.t("name", "陆行鸟之魂")
			.t("desc", "召唤陆行鸟。");
	}

	{ image = ConsumSummorDict.CHOCOBO_EGG_0; }
	@Override protected LegacyPet hatchling() { return new Chocobo(); }
	@Override public int value() { return 500 * quantity; }
}
