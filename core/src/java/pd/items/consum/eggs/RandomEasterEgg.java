/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.mobs.pets.Bunny;
import pd.actors.mobs.pets.CocoCat;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.Velocirooster;
import render.utils.math.Random;
import pd.messages.InlineText;

public class RandomEasterEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEasterEgg.class)
			.t("name", "随机复活节之魂")
			.t("desc", "随机召唤三种复活节宠物之一。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected LegacyPet hatchling() {
		switch (Random.Int(3)) {
			case 0: return new Bunny();
			case 1: return new CocoCat();
			default: return new Velocirooster();
		}
	}
	@Override public int value() { return 500 * quantity; }
}
