package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.effects.Speck;

public class PotionOfOverHealing extends SpsPotion {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public void apply(Hero hero) {
		PotionOfHealing.cure(hero);
		hero.HP = hero.HT;
		Buff.affect(hero, Healing.class).setHeal(hero.HT, 0.25f, 0, true);
		Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.lvl * 2));
		hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
	}
	@Override public int value() { return 30 * quantity; }
}
