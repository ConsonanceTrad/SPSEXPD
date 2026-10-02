package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Arcane;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;

public class MagicPill extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MagicPill.class)
			.t("name", "奥术药丸")
			.t("ac_use", "服用")
			.t("desc", "在一段时间内提升法强。\n使用_2份肉，1份种子，1份药水_炼金");
	}




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
