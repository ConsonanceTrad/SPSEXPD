package pd.items.consum.food.processed;

import pd.actors.buffs.Barrier;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class WishPetal extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.WISH_PETAL; }
	static {
		InlineText.of(WishPetal.class)
			.t("name", "许愿花瓣")
			.t("desc", "七色堇上摘下的一片花瓣，看上去不是食物。可以作为炼金或烹饪原料。");
	}

}
