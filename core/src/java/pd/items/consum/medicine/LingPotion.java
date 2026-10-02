package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.LingBless;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;

public class LingPotion extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LingPotion.class)
			.t("name", "澪祷星瓶")
			.t("desc", "一瓶来自澪的圣水，能极大提升使用者的能力。\n来自澪的回礼");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, LingBless.class, 200f);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.STAR), 0.2f, 3);
	}
	@Override public int value() { return 50 * quantity; }
}
