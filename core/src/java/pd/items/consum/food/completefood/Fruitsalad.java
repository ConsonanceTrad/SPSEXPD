/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;

public class Fruitsalad extends CompleteFood {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 130f; }
	@Override protected void doEat(Hero hero) {
		heal(hero, hero.HT / 3);
		Buff.affect(hero, BerryRegeneration.class).level(Math.max(hero.HT / 2, 30));
	}
	@Override public int value() { return 2 * quantity; }
}
