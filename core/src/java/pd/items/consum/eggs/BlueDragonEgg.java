/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.BlueDragon;
import pd.actors.mobs.pets.LegacyPet;

public class BlueDragonEgg extends Egg {
	{ image = ConsumSummorDict.BLUE_DRAGON_EGG_0; freezes = 20; }
	@Override protected LegacyPet hatchling() { return new BlueDragon(); }
	@Override public int value() { return 500 * quantity; }
}
