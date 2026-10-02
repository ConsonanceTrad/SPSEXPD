/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.actors.mobs.pets.ButterflyPet;
import pd.actors.mobs.pets.Datura;
import pd.actors.mobs.pets.Monkey;
import pd.actors.mobs.pets.PigPet;

public class RandomColEgg extends RandomPetEgg {
	public RandomColEgg() { super(ButterflyPet.class, Monkey.class, PigPet.class, Datura.class); }
}
