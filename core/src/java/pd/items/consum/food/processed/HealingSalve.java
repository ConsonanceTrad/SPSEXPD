package pd.items.consum.food.processed;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class HealingSalve extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.HEALING_SALVE; }
	static {
		InlineText.of(HealingSalve.class)
			.t("name", "愈敷液")
			.t("desc", "由生命草煎出的药液，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Healing.class).setHeal(Math.max(10, hero.HT / 2), 0.25f, 0);
	}
}
