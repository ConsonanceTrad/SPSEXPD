/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.LitDemon;
public class LitDemonEgg extends Egg { { image = ConsumSummorDict.LIT_DEMON_EGG_0; } @Override protected LegacyPet hatchling() { return new LitDemon(); } @Override public int value() { return 500 * quantity; } }
