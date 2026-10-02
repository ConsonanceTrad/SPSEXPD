/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.Kodora;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;
public class KodoraEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(KodoraEgg.class)
			.t("name", "柯多拉之魂")
			.t("desc", "召唤柯多拉。");
	}
 { image = ConsumSummorDict.KODORA_EGG_0; } @Override protected LegacyPet hatchling() { return new Kodora(); } @Override public int value() { return 500 * quantity; } }
