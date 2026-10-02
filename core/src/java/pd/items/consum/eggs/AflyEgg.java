/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.Abi;
import pd.actors.mobs.pets.LegacyPet;

/** Alfred's whistle, represented by an egg action in the original pet system. */
public class AflyEgg extends Egg {
	{ image = ConsumSummorDict.AFLY_EGG_0; }
	@Override protected LegacyPet hatchling() { return new Abi(); }
	@Override public int value() { return 500 * quantity; }
}
