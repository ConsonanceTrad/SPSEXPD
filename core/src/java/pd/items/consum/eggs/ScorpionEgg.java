/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.Scorpion;

public class ScorpionEgg extends Egg {
	{ image = ConsumSummorDict.SCORPION_EGG_0; moves = 2000; }
	@Override protected LegacyPet hatchling() { return new Scorpion(); }
	@Override public int value() { return 500 * quantity; }
}
