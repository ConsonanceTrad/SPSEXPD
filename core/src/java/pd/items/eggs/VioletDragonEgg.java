/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.VioletDragon;
import pd.sprites.ItemSpriteSheet;

public class VioletDragonEgg extends Egg {
	{ image = ItemSpriteSheet.VIOLET_DRAGON_EGG; poisons = 20; }
	@Override protected LegacyPet hatchling() { return new VioletDragon(); }
	@Override public int value() { return 500 * quantity; }
}
