/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.eggs.randomone;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.GentleCrab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Kodora;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.RibbonRat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets.Snake;

public class RandomAtkEgg extends RandomPetEgg {
	public RandomAtkEgg() { super(Kodora.class, Snake.class, RibbonRat.class, GentleCrab.class); }
}
