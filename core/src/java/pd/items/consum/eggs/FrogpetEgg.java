/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.FrogPet;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;
public class FrogpetEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FrogpetEgg.class)
			.t("name", "呆头蛙之魂")
			.t("desc", "召唤呆头蛙。");
	}


 { image = ConsumSummorDict.FROG_PET_EGG_0; } @Override protected LegacyPet hatchling() { return new FrogPet(); } @Override public int value() { return 500 * quantity; } }
