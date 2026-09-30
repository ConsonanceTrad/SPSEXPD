/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.Bunny;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;

public class EasterEgg extends Egg {
	{ image = ItemSpriteSheet.RABBIT_PET_EGG; }
	@Override protected LegacyPet hatchling() { return new Bunny(); }
	@Override public int value() { return 500 * quantity; }
}
