package pd.items.consum.food.processed;

import pd.actors.buffs.Buff;
import pd.actors.buffs.FireImbue;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Capsaicin extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.CAPSAICIN; }
	static {
		InlineText.of(Capsaicin.class)
			.t("name", "浓缩辣素")
			.t("desc", "从辣椒中提炼的纯粹辣素，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
	}
}
