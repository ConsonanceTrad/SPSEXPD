package pd.items.consum.food.processed;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.SuperArcane;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class FruitThread extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_THREAD; }
	static {
		InlineText.of(FruitThread.class)
			.t("name", "五色果丝")
			.t("desc", "由无味果切出的五彩果丝，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Bless.class, 50f);
		Buff.affect(hero, SuperArcane.class, 40f).level(5);
	}
}
