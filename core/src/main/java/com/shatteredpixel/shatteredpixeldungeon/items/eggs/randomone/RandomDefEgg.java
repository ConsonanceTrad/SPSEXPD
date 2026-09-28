/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Chocobo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.DogPet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Fly;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Spider;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Stone;

public class RandomDefEgg extends RandomPetEgg {
	public RandomDefEgg() { super(DogPet.class, Chocobo.class, Fly.class, Stone.class, Spider.class); }
}
