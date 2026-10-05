package pd.items.consum.food.processed;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.FrostImbue;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class CoolingOil extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.COOLING_OIL; }
	static {
		InlineText.of(CoolingOil.class)
			.t("name", "清凉油")
			.t("desc", "由冰薄荷调制的清凉药油，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.detach(hero, Burning.class);
		Buff.affect(hero, FrostImbue.class, FrostImbue.DURATION);
	}
}
