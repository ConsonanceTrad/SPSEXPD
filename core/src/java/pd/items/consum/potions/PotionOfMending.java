package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.effects.Speck;

public class PotionOfMending extends SpsPotion {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public void apply(Hero hero) {
		PotionOfHealing.cure(hero);
		Buff.affect(hero, Healing.class).setHeal(Math.max(hero.HT / 4, 30), 0.25f, 0, true);
		hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
	}
	@Override public int value() { return 20 * quantity; }
}
