package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class PotionOfShield extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfShield.class)
			.t("name", "护盾药水")
			.t("desc", "以硅花种子酿成的防御药水。饮用后获得物理护盾与奥术护甲，溅出的药液还能使生物短暂麻痹。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override public void apply(Hero hero) {
		Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.HT / 3));
		Buff.affect(hero, ArcaneArmor.class).set(Math.max(1, hero.HT / 3), 30);
		Sample.INSTANCE.play(Assets.Sounds.MELD);
	}
	@Override public void shatter(int cell) {
		Char ch = Actor.findChar(cell);
		if (ch != null) Buff.prolong(ch, Paralysis.class, 5f);
		super.shatter(cell);
	}
	@Override public int value() { return 40 * quantity; }
}
