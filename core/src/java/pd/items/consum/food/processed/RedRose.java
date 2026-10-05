package pd.items.consum.food.processed;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class RedRose extends Processed {
	static {
		InlineText.of(RedRose.class)
			.t("name", "玫瑰花")
			.t("desc", "坚果藤上开出的玫瑰，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	{ image = ConsumPotionSeedSeedDict.RED_ROSE; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Healing.class).setHeal(Math.max(10, hero.HT / 4), 0.25f, 0);
		Buff.prolong(hero, Bless.class, 40f);
	}
}
