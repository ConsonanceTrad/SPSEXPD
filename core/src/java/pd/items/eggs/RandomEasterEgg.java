/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.mobs.pets.Bunny;
import pd.actors.mobs.pets.CocoCat;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.Velocirooster;
import render.utils.math.Random;

public class RandomEasterEgg extends Egg {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected LegacyPet hatchling() {
		switch (Random.Int(3)) {
			case 0: return new Bunny();
			case 1: return new CocoCat();
			default: return new Velocirooster();
		}
	}
	@Override public int value() { return 500 * quantity; }
}
