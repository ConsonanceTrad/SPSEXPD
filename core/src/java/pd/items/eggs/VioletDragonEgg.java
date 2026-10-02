/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.VioletDragon;

public class VioletDragonEgg extends Egg {
	{ image = ConsumSummorDict.VIOLET_DRAGON_EGG_0; poisons = 20; }
	@Override protected LegacyPet hatchling() { return new VioletDragon(); }
	@Override public int value() { return 500 * quantity; }
}
