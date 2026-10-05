package pd.items.consum.food.processed;

import pd.actors.buffs.Buff;
import pd.actors.buffs.ToxicImbue;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class ToxicExtract extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.TOXIC_EXTRACT; }
	static {
		InlineText.of(ToxicExtract.class)
			.t("name", "浓缩毒液")
			.t("desc", "从毒茄子中榨出的毒液，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION);
	}
}
