/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.PigPet;
public class PigpetEgg extends Egg {
	{ image = ConsumSummorDict.PIG_PET_EGG_0; }
	@Override protected LegacyPet hatchling() { return new PigPet(); }
	@Override public int value() { return 500 * quantity; }
}
