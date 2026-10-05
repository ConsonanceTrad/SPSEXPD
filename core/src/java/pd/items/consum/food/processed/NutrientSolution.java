package pd.items.consum.food.processed;

import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class NutrientSolution extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.NUTRIENT_SOLUTION; }
	static {
		InlineText.of(NutrientSolution.class)
			.t("name", "营养液")
			.t("desc", "从萝卜中提纯的营养液，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Healing.class).setHeal(Math.max(8, hero.HT / 3), 0.25f, 0);
		Buff.affect(hero, Barrier.class).incShield(Math.max(2, hero.HT / 6));
	}
}
