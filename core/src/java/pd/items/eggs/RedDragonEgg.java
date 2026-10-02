/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.RedDragon;

public class RedDragonEgg extends Egg {
	{ image = ConsumSummorDict.RED_DRAGON_EGG_0; burns = 20; }
	@Override protected LegacyPet hatchling() { return new RedDragon(); }
	@Override public int value() { return 500 * quantity; }
}
