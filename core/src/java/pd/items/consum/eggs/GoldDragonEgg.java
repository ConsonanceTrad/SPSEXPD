/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.atlas.items.ConsumSummorDict;

import pd.actors.mobs.pets.BugDragon;
import pd.actors.mobs.pets.GoldDragon;
import pd.actors.mobs.pets.LegacyPet;
import render.utils.math.Random;

import java.util.Calendar;

public class GoldDragonEgg extends Egg {
	{
		image = ConsumSummorDict.GOLD_DRAGON_EGG_0;
		moves = 2000; burns = freezes = poisons = lits = darks = lights = 20;
	}
	@Override protected LegacyPet hatchling() {
		return Calendar.getInstance().get(Calendar.MONTH) == Calendar.SEPTEMBER || Random.Int(50) == 0
				? new BugDragon() : new GoldDragon();
	}
	@Override public int value() { return 500 * quantity; }
}
