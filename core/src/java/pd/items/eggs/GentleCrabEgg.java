/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.GentleCrab;
import pd.actors.mobs.pets.LegacyPet;
public class GentleCrabEgg extends Egg { { image = ConsumSummorDict.GENTLE_CRAB_EGG_0; } @Override protected LegacyPet hatchling() { return new GentleCrab(); } @Override public int value() { return 500 * quantity; } }
