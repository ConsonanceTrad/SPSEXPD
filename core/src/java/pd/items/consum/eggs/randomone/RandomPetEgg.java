/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.mobs.pets.LegacyPet;
import pd.items.consum.eggs.Egg;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

/** Shared implementation for the original category and monthly random souls. */
public abstract class RandomPetEgg extends Egg {

	private final Class<? extends LegacyPet>[] candidates;

	@SafeVarargs
	protected RandomPetEgg(Class<? extends LegacyPet>... candidates) {
		this.candidates = candidates;
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	protected LegacyPet hatchling() {
		return Reflection.newInstance(candidates[Random.Int(candidates.length)]);
	}

	public Class<? extends LegacyPet>[] possiblePets() {
		return candidates.clone();
	}

	@Override public int value() { return 500 * quantity; }
}
