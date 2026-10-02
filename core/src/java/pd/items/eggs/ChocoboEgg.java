/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.Chocobo;
import pd.actors.mobs.pets.LegacyPet;

public class ChocoboEgg extends Egg {
	{ image = ConsumSummorDict.CHOCOBO_EGG_0; }
	@Override protected LegacyPet hatchling() { return new Chocobo(); }
	@Override public int value() { return 500 * quantity; }
}
