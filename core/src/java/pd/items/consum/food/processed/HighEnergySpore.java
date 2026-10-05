package pd.items.consum.food.processed;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Dewcharge;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class HighEnergySpore extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.HIGH_ENERGY_SPORE; }
	static {
		InlineText.of(HighEnergySpore.class)
			.t("name", "高能孢子")
			.t("desc", "露珠菌孢中最饱满的孢子，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Dewcharge.class, 200f);
	}
}
