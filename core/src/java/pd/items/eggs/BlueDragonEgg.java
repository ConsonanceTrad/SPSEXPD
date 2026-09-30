/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.BlueDragon;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;

public class BlueDragonEgg extends Egg {
	{ image = ItemSpriteSheet.BLUE_DRAGON_EGG; freezes = 20; }
	@Override protected LegacyPet hatchling() { return new BlueDragon(); }
	@Override public int value() { return 500 * quantity; }
}
