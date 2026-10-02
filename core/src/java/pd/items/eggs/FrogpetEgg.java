/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.FrogPet;
import pd.actors.mobs.pets.LegacyPet;
public class FrogpetEgg extends Egg { { image = ConsumSummorDict.FROG_PET_EGG_0; } @Override protected LegacyPet hatchling() { return new FrogPet(); } @Override public int value() { return 500 * quantity; } }
