package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Rhythm2;
import pd.actors.buffs.Rhythm;
import pd.actors.buffs.WarGroove;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.effects.Speck;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

public class Musicpill extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Musicpill.class)
			.t("name", "节奏药丸")
			.t("desc", "在一段时间内提升自信。\n使用_2份肉，1份种子，1份原石_炼金");
	}



	{ image = ConsumPotionSeedBasicPotionDict.PILL; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, Rhythm.class, 800f);
		if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.PERFORMER) {
			Buff.affect(hero, WarGroove.class);
		}
		if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.SUPERSTAR) {
			Buff.affect(hero, Rhythm2.class, 800f);
		}
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 50 * quantity; }
}
