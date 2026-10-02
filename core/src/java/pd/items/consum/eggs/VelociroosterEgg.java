/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.Velocirooster;
import pd.messages.InlineText;
public class VelociroosterEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(VelociroosterEgg.class)
			.t("name", "坤哥之魂")
			.t("desc", "召唤坤坤。");
	}

	{ image = ConsumSummorDict.VELOCIROOSTER_EGG_0; }
	@Override protected LegacyPet hatchling() { return new Velocirooster(); }
	@Override public int value() { return 500 * quantity; }
}
