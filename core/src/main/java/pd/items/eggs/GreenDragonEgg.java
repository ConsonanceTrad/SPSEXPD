/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.GreenDragon;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;

public class GreenDragonEgg extends Egg {
	{ image = ItemSpriteSheet.GREEN_DRAGON_EGG; lits = 20; }
	@Override protected LegacyPet hatchling() { return new GreenDragon(); }
	@Override public int value() { return 500 * quantity; }
}
