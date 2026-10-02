/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;

public class Chickennugget extends CompleteFood {
	{ image = ConsumFoodFoodDict.CHICKENNUGGET; energy = 170f; }
	@Override protected void doEat(Hero hero) { Buff.affect(hero, AttackUp.class, 50f).level(20); }
	@Override public int value() { return 2 * quantity; }
}
