/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs.randomone;

import pd.actors.mobs.pets.LegacyPet;
import pd.items.eggs.Egg;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;
import watabou.utils.Reflection;

/** Shared implementation for the original category and monthly random souls. */
public abstract class RandomPetEgg extends Egg {

	private final Class<? extends LegacyPet>[] candidates;

	@SafeVarargs
	protected RandomPetEgg(Class<? extends LegacyPet>... candidates) {
		this.candidates = candidates;
		image = ItemSpriteSheet.SPS_PET_EGG;
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
