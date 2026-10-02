package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;

public class PotionOfOverHealing extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfOverHealing.class)
			.t("name", "生命药水")
			.t("desc", "以吞星花种子酿成的强效药水。它能清除异常、回满生命并持续治疗，将溢出的活力转化为护盾。");
	}



	{ image = SpecificPlaceHolderDict.POTION_HOLDER_0; }
	@Override public void apply(Hero hero) {
		PotionOfHealing.cure(hero);
		hero.HP = hero.HT;
		Buff.affect(hero, Healing.class).setHeal(hero.HT, 0.25f, 0, true);
		Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.lvl * 2));
		hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
	}
	@Override public int value() { return 30 * quantity; }
}
