/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;

public class Herbmeat extends CompleteFood {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 180f; }
	@Override protected void doEat(Hero hero) { Buff.affect(hero, AttackUp.class, 70f).level(30); }
	@Override public int value() { return 2 * quantity; }
}
