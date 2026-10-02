package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.TargetShoot;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

public class Shootpill extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Shootpill.class)
			.t("name", "神射药丸")
			.t("desc", "在一段时间内提升射击力。\n使用_3份肉，1份种子_炼金");
	}



	{ image = ConsumPotionSeedBasicPotionDict.PILL; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, TargetShoot.class, 800f);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 50 * quantity; }
}
