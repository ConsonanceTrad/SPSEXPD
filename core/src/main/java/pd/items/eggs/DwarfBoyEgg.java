/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.DwarfBoy;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;

public class DwarfBoyEgg extends Egg {
	{ image = ItemSpriteSheet.DWARF_BOY_EGG; }
	@Override protected LegacyPet hatchling() { return new DwarfBoy(); }
	@Override public int value() { return 500 * quantity; }
}
