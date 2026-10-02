/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.Datura;
import pd.actors.mobs.pets.LegacyPet;

public class DaturaEgg extends Egg {
	{ image = ConsumSummorDict.DATURA_EGG_0; }
	@Override protected LegacyPet hatchling() { return new Datura(); }
	@Override public int value() { return 500 * quantity; }
}
