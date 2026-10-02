package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.LingBless;
import pd.actors.hero.Hero;
import pd.effects.Speck;

public class LingPotion extends Pill {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, LingBless.class, 200f);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.STAR), 0.2f, 3);
	}
	@Override public int value() { return 50 * quantity; }
}
