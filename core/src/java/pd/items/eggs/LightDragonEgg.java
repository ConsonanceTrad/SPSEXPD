/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.LightDragon;

public class LightDragonEgg extends Egg {
	{ image = ConsumSummorDict.LIGHT_DRAGON_EGG_0; darks = 20; }
	@Override protected LegacyPet hatchling() { return new LightDragon(); }
	@Override public int value() { return 500 * quantity; }
}
