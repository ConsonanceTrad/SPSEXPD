/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs.randomone;

import pd.actors.mobs.pets.GentleCrab;
import pd.actors.mobs.pets.Kodora;
import pd.actors.mobs.pets.RibbonRat;
import pd.actors.mobs.pets.Snake;

public class RandomAtkEgg extends RandomPetEgg {
	public RandomAtkEgg() { super(Kodora.class, Snake.class, RibbonRat.class, GentleCrab.class); }
}
