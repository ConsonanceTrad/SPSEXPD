/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.YearPet;

/** The guaranteed soul dropped by the Spring Festival year beast. */
public class YearPetEgg extends Egg {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override protected LegacyPet hatchling() { return new YearPet(); }
	@Override public int value() { return 500 * quantity; }
}
