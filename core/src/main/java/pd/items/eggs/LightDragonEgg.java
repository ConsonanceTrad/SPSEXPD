/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.LightDragon;
import pd.sprites.ItemSpriteSheet;

public class LightDragonEgg extends Egg {
	{ image = ItemSpriteSheet.LIGHT_DRAGON_EGG; darks = 20; }
	@Override protected LegacyPet hatchling() { return new LightDragon(); }
	@Override public int value() { return 500 * quantity; }
}
