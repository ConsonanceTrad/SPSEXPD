/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Light;
import pd.actors.hero.Hero;

public class PerfectFood extends CompleteFood {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 600f; }
	@Override protected void doEat(Hero hero) {
		increaseMaxHealth(hero, 3, 7);
		Buff.affect(hero, Bless.class, 50f);
		Buff.affect(hero, Light.class, 50f);
		Buff.affect(hero, HasteBuff.class, 25f);
		Buff.affect(hero, Levitation.class, 25f);
	}
	@Override public int value() { return 50 * quantity; }
}
