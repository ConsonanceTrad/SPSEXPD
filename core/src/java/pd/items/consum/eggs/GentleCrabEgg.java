/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.GentleCrab;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;
public class GentleCrabEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GentleCrabEgg.class)
			.t("name", "绅士蟹之魂")
			.t("desc", "召唤绅士蟹。");
	}
 { image = ConsumSummorDict.GENTLE_CRAB_EGG_0; } @Override protected LegacyPet hatchling() { return new GentleCrab(); } @Override public int value() { return 500 * quantity; } }
