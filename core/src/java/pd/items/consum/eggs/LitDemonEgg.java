/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.LitDemon;
import pd.messages.InlineText;
public class LitDemonEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LitDemonEgg.class)
			.t("name", "链锯魔之魂")
			.t("desc", "召唤链锯魔。");
	}


 { image = ConsumSummorDict.LIT_DEMON_EGG_0; } @Override protected LegacyPet hatchling() { return new LitDemon(); } @Override public int value() { return 500 * quantity; } }
