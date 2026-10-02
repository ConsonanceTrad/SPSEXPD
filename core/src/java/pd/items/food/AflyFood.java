/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AflyBless;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;

/** The original Fushigi-no rice ball made by Alfred. */
public class AflyFood extends Food {
	{
		image = ConsumFoodFoodDict.AFLY_FOOD;
		energy = 200f;
	}
	@Override protected void satisfy(Hero hero) {
		super.satisfy(hero);
		Buff.affect(hero, AflyBless.class, 150f);
	}
	@Override public int value() { return 2 * quantity; }
}
