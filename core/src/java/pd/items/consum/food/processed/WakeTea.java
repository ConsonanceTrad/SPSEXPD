package pd.items.consum.food.processed;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.items.consum.potions.PotionOfHealing;
import pd.messages.InlineText;

public class WakeTea extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.WAKE_TEA; }
	static {
		InlineText.of(WakeTea.class)
			.t("name", "醒神细茶")
			.t("desc", "用好梦叶冲泡的细茶，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		PotionOfHealing.cure(hero);
		Buff.detach(hero, Vertigo.class);
		Buff.prolong(hero, MindVision.class, 30f);
	}
}
