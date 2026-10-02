/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.FoxHelper;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;
public class FoxHelperEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FoxHelperEgg.class)
			.t("name", "狐女仆之魂")
			.t("desc", "召唤狐女仆。");
	}
 { image = ConsumSummorDict.FOX_HELPER_EGG_0; } @Override protected LegacyPet hatchling() { return new FoxHelper(); } @Override public int value() { return 500 * quantity; } }
