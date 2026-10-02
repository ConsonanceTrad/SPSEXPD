package pd.items.consum.potions;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.items.consum.potions.elixirs.ElixirOfMight;
import pd.messages.InlineText;

public class PotionOfMight extends SpsPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfMight.class)
			.t("name", "根骨药水")
			.t("desc", "以转换笼种子酿成的强身药水。它能暂时提高生命上限，并提供持久的物理防护。");
	}



	{ image = SpecificPlaceHolderDict.POTION_HOLDER_0; }
	@Override public void apply(Hero hero) {
		Buff.affect(hero, Barkskin.class).set(8 + hero.lvl / 2, 360);
		Buff.affect(hero, ElixirOfMight.HTBoost.class).reset();
		hero.updateHT(true);
		hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 200 * quantity; }
}
