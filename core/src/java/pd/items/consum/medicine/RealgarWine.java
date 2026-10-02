package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.FireImbue;
import pd.actors.buffs.ToxicImbue;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class RealgarWine extends Pill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RealgarWine.class)
			.t("name", "雄黄酒")
			.t("desc", "提供火焰抗性和剧毒抗性。\n使用_1份水，1份烈焰花种子，1份地缚根种子_炼金");
	}



	{ image = ConsumFoodFoodDict.REALGAR_WINE; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
		Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 50 * quantity; }
}
