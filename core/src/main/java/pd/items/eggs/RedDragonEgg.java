/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.RedDragon;
import pd.sprites.ItemSpriteSheet;

public class RedDragonEgg extends Egg {
	{ image = ItemSpriteSheet.RED_DRAGON_EGG; burns = 20; }
	@Override protected LegacyPet hatchling() { return new RedDragon(); }
	@Override public int value() { return 500 * quantity; }
}
