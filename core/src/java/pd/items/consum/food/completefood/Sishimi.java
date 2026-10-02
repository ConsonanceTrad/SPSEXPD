/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.hero.Hero;

public class Sishimi extends CompleteFood {
	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 180f;
	}
	@Override protected void doEat(Hero hero) { Buff.affect(hero, MagicArmor.class).level(hero.HT / 5); }
	@Override public int value() { return 3 * quantity; }
}
