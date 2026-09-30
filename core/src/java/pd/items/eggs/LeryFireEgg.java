/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.LeryFire;
import pd.sprites.ItemSpriteSheet;

public class LeryFireEgg extends Egg {
	{ image = ItemSpriteSheet.LERY_FIRE_EGG; moves = 50; burns = freezes = poisons = lits = 5; }
	@Override protected LegacyPet hatchling() { return new LeryFire(); }
	@Override public int value() { return 500 * quantity; }
}
