package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.buffs.Buff;
import pd.actors.buffs.GlassShield;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;
import render.noosa.audio.Sample;

/** SPSEXPD: 由石英花果酿造，饮用后获得玻璃保护。 */
public class PotionOfGlass extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfGlass.class)
			.t("name", "玻璃药剂")
			.t("desc", "以石英花果酿成的防护药剂。饮用后获得玻璃保护，能抵挡数次伤害。");
	}

	{ image = SpecificPlaceHolderDict.POTION_HOLDER_0; }

	@Override public void apply(Hero hero) {
		Buff.affect(hero, GlassShield.class).turns(10);
		Sample.INSTANCE.play(Assets.Sounds.MELD);
		hero.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.4f, 4);
	}

	@Override public int value() { return 40 * quantity; }
}
