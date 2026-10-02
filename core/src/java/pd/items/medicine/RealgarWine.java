package pd.items.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.FireImbue;
import pd.actors.buffs.ToxicImbue;
import pd.actors.hero.Hero;
import pd.effects.Speck;

public class RealgarWine extends Pill {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
		Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 50 * quantity; }
}
