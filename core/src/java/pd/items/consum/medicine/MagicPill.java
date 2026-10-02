package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Arcane;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.Speck;

public class MagicPill extends Pill {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	protected void onUse(Hero hero) {
		Buff.affect(hero, Arcane.class, 50f);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}

	@Override public int value() { return 50 * quantity; }
}
