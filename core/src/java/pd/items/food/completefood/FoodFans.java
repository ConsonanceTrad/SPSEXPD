/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;

public class FoodFans extends CompleteFood {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 150f; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, ShieldArmor.class).level(hero.HT / 2);
		Buff.affect(hero, Bless.class, 50f);
	}
	@Override public int value() { return 20 * quantity; }
}
