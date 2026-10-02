/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.Bunny;
import pd.actors.mobs.pets.LegacyPet;

public class EasterEgg extends Egg {
	{ image = ConsumSummorDict.RABBIT_PET_EGG_0; }
	@Override protected LegacyPet hatchling() { return new Bunny(); }
	@Override public int value() { return 500 * quantity; }
}
