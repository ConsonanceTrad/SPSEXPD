/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;

public class Chocolate extends CompleteFood {
	{ image = ConsumFoodFoodDict.CHOCOLATE; energy = 300f; }
	@Override protected void doEat(Hero hero) { Buff.affect(hero, ShieldArmor.class).level(hero.HT); }
	@Override public int value() { return 60 * quantity; }
}
