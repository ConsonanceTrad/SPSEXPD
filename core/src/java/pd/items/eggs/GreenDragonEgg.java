/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.GreenDragon;
import pd.actors.mobs.pets.LegacyPet;

public class GreenDragonEgg extends Egg {
	{ image = ConsumSummorDict.GREEN_DRAGON_EGG_0; lits = 20; }
	@Override protected LegacyPet hatchling() { return new GreenDragon(); }
	@Override public int value() { return 500 * quantity; }
}
