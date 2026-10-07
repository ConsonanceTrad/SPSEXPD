package pd.items.consum.food.processed;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.SuperArcane;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class DigestiveFluid extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.DIGESTIVE_FLUID; }
	static {
		InlineText.of(DigestiveFluid.class)
			.t("name", "消化液")
			.t("desc", "从吞星花中取出的消化液，看上去不是食物。可以作为炼金或烹饪原料。");
	}

}
