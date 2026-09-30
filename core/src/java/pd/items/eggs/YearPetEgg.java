/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.YearPet;
import pd.sprites.ItemSpriteSheet;

/** The guaranteed soul dropped by the Spring Festival year beast. */
public class YearPetEgg extends Egg {

	{
		image = ItemSpriteSheet.YEAR_PET_EGG;
	}

	@Override protected LegacyPet hatchling() { return new YearPet(); }
	@Override public int value() { return 500 * quantity; }
}
