/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.Kodora;
import pd.actors.mobs.pets.LegacyPet;
public class KodoraEgg extends Egg { { image = ConsumSummorDict.KODORA_EGG_0; } @Override protected LegacyPet hatchling() { return new Kodora(); } @Override public int value() { return 500 * quantity; } }
