/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.BugDragon;
import pd.actors.mobs.pets.LegacyPet;

/** Retains the unusual class spelling used by SPS-PD 0.9.8. */
public class BugDragonEGG extends Egg {
	{
		image = ConsumSummorDict.BUG_DRAGON_EGG_0;
		moves = 2000; burns = freezes = poisons = lits = darks = lights = 20;
	}
	@Override protected LegacyPet hatchling() { return new BugDragon(); }
	@Override public int value() { return 500 * quantity; }
}
