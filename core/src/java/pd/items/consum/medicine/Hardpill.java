package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

public class Hardpill extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Hardpill.class)
			.t("name", "硬化药丸")
			.t("desc", "在一段时间内提升防御。\n使用_2份肉，1份蔬菜，1份原石_炼金");
	}



	{ image = ConsumPotionSeedBasicPotionDict.PILL; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, DefenceUp.class, 800f).level(50);
	}
	@Override public int value() { return 50 * quantity; }
}
