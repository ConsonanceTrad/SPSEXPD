/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.ConsumSummorDict;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.Velocirooster;
public class VelociroosterEgg extends Egg {
	{ image = ConsumSummorDict.VELOCIROOSTER_EGG_0; }
	@Override protected LegacyPet hatchling() { return new Velocirooster(); }
	@Override public int value() { return 500 * quantity; }
}
