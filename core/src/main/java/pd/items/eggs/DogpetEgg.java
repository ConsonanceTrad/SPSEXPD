/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.DogPet;
import pd.actors.mobs.pets.LegacyPet;
import pd.sprites.ItemSpriteSheet;

public class DogpetEgg extends Egg {
	{ image = ItemSpriteSheet.DOG_PET_EGG; }
	@Override protected LegacyPet hatchling() { return new DogPet(); }
	@Override public int value() { return 500 * quantity; }
}
