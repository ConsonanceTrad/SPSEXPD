/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.mobs.pets.Haro;
import pd.actors.mobs.pets.LegacyPet;
public class HaroEgg extends Egg {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected LegacyPet hatchling() { return new Haro(); }
	@Override public int value() { return 500 * quantity; }
}
