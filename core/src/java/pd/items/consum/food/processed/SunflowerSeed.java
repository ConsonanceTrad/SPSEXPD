package pd.items.consum.food.processed;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Haste;
import pd.actors.buffs.MindVision;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class SunflowerSeed extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.SUNFLOWER_SEED; }
	static {
		InlineText.of(SunflowerSeed.class)
			.t("name", "葵花籽")
			.t("desc", "向日葵的种子，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, MindVision.class, MindVision.DURATION);
		Buff.affect(hero, Haste.class, 10f);
	}
}
