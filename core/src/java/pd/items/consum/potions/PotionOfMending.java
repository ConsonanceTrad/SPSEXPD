package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;

public class PotionOfMending extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfMending.class)
			.t("name", "恢复药水")
			.t("desc", "以坚果藤种子酿成的恢复药水。它能清除常见异常状态，并持续修复大量生命。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public void apply(Hero hero) {
		PotionOfHealing.cure(hero);
		Buff.affect(hero, Healing.class).setHeal(Math.max(hero.HT / 4, 30), 0.25f, 0, true);
		hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
	}
	@Override public int value() { return 20 * quantity; }
}
