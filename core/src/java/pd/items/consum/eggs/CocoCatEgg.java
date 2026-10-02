/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.mobs.pets.CocoCat;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;
import pd.atlas.items.ConsumSummorDict;
public class CocoCatEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CocoCatEgg.class)
			.t("name", "爆破之魂")
			.t("desc", "炸弹，炸弹，炸弹！召唤椰子培养的爆破猫。");
	}



	{ image = ConsumSummorDict.STONE_PET_EGG_0; }
	@Override protected LegacyPet hatchling() { return new CocoCat(); }
	@Override public int value() { return 500 * quantity; }
}
