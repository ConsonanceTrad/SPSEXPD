/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.actors.mobs.pets.Chocobo;
import pd.actors.mobs.pets.DogPet;
import pd.actors.mobs.pets.Fly;
import pd.actors.mobs.pets.Spider;
import pd.actors.mobs.pets.Stone;

public class RandomDefEgg extends RandomPetEgg {
	public RandomDefEgg() { super(DogPet.class, Chocobo.class, Fly.class, Stone.class, Spider.class); }
}
