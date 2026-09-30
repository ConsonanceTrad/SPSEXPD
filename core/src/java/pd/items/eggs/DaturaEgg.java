/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.Datura;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;

public class DaturaEgg extends Egg {
	{ image = ItemSpriteSheet.DATURA_EGG; }
	@Override protected LegacyPet hatchling() { return new Datura(); }
	@Override public int value() { return 500 * quantity; }
}
