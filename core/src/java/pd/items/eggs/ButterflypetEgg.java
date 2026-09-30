/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.ButterflyPet;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;

public class ButterflypetEgg extends Egg {
	{ image = ItemSpriteSheet.BUTTERFLY_EGG; }
	@Override protected LegacyPet hatchling() { return new ButterflyPet(); }
	@Override public int value() { return 500 * quantity; }
}
