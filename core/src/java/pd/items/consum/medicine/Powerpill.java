package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Muscle;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

public class Powerpill extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Powerpill.class)
			.t("name", "力量药丸")
			.t("desc", "在一段时间内提升力量。\n使用_3份肉，1份蔬菜_炼金");
	}



	{ image = ConsumPotionSeedBasicPotionDict.PILL; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, Muscle.class, 1440f);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 50 * quantity; }
}
