package pd.items.consum.food.processed;

import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Adhesive extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.ADHESIVE; }
	static {
		InlineText.of(Adhesive.class)
			.t("name", "粘合剂")
			.t("desc", "由榴莲熬制的黏稠胶体，看上去不是食物。可以作为炼金或烹饪原料。");
	}

}
