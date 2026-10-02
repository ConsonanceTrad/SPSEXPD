package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;

public class Smashpill extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Smashpill.class)
			.t("name", "增幅药丸")
			.t("desc", "在一段时间内提升伤害。\n使用_2份肉，1份蔬菜，1份药水_炼金");
	}



	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, AttackUp.class, 800f).level(50);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 50 * quantity; }
}
