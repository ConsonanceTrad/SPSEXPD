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

}
