/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.DwarfBoy;
import pd.actors.mobs.pets.LegacyPet;
import pd.messages.InlineText;

public class DwarfBoyEgg extends Egg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DwarfBoyEgg.class)
			.t("name", "矮人学徒")
			.t("desc", "召唤矮人学徒。");
	}



	{ image = ConsumSummorDict.DWARF_BOY_EGG_0; }
	@Override protected LegacyPet hatchling() { return new DwarfBoy(); }
	@Override public int value() { return 500 * quantity; }
}
