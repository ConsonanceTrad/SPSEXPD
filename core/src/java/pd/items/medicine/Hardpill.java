package pd.items.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.hero.Hero;

public class Hardpill extends Pill {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, DefenceUp.class, 800f).level(50);
	}
	@Override public int value() { return 50 * quantity; }
}
