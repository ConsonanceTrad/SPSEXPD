/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.BlueGirl;
import pd.actors.mobs.pets.LegacyPet;

public class BlueGirlEgg extends Egg {
	{ image = ConsumSummorDict.BLUE_GIRL_EGG_0; poisons = 30; lights = 66; }
	@Override protected LegacyPet hatchling() { return new BlueGirl(); }
	@Override public int value() { return 500 * quantity; }
}
