/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.SuperArcane;
import pd.actors.hero.Hero;

public class Frenchfries extends CompleteFood {
	{ image = ConsumFoodFoodDict.FRENCH_FRIES; energy = 150f; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, ShieldArmor.class).level(hero.HT / 2);
		Buff.affect(hero, Recharging.class, 20f);
		Buff.affect(hero, SuperArcane.class, 40f).level(5);
	}
	@Override public int value() { return 20 * quantity; }
}
