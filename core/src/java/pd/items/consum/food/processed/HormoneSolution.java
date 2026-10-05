package pd.items.consum.food.processed;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Haste;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class HormoneSolution extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.HORMONE_SOLUTION; }
	static {
		InlineText.of(HormoneSolution.class)
			.t("name", "激素溶液")
			.t("desc", "由酢浆草萃取的溶液，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Haste.class, 20f);
		Buff.prolong(hero, Bless.class, 20f);
	}
}
