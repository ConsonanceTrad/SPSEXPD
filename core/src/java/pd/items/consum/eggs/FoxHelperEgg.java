/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.FoxHelper;
import pd.actors.mobs.pets.LegacyPet;
public class FoxHelperEgg extends Egg { { image = ConsumSummorDict.FOX_HELPER_EGG_0; } @Override protected LegacyPet hatchling() { return new FoxHelper(); } @Override public int value() { return 500 * quantity; } }
