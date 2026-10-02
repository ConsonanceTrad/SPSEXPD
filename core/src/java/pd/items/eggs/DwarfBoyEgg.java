/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.DwarfBoy;
import pd.actors.mobs.pets.LegacyPet;

public class DwarfBoyEgg extends Egg {
	{ image = ConsumSummorDict.DWARF_BOY_EGG_0; }
	@Override protected LegacyPet hatchling() { return new DwarfBoy(); }
	@Override public int value() { return 500 * quantity; }
}
