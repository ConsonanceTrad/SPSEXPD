package pd.items.consum.food.processed;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Levitation;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Perfume extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.PERFUME; }
	static {
		InlineText.of(Perfume.class)
			.t("name", "名贵香水")
			.t("desc", "由郁金香酿制的昂贵香水，看上去不是食物。可以作为炼金或烹饪原料。");
	}

}
