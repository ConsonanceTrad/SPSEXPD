package pd.items.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.utils.GLog;

public class PotionOfMixing extends SpsPotion {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public void apply(Hero hero) {
		hero.improveCombatSkills(1);
		Buff.prolong(hero, Recharging.class, 30f);
		hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
		GLog.p(Messages.get(this, "skillup"));
	}
	@Override public int value() { return 100 * quantity; }
}
