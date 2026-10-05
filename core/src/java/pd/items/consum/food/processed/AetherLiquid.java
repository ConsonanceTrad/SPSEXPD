package pd.items.consum.food.processed;

import pd.actors.buffs.Arcane;
import pd.actors.buffs.Buff;
import pd.actors.buffs.SuperArcane;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class AetherLiquid extends Processed {
	{ image = pd.atlas.items.ConsumPotionSeedSeedDict.AETHER_LIQUID; }
	static {
		InlineText.of(AetherLiquid.class)
			.t("name", "韵魔原液")
			.t("desc", "转换笼中沉淀的原液，看上去不是食物。可以作为炼金或烹饪原料。");
	}

	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Arcane.class, 50f);
		Buff.affect(hero, SuperArcane.class, 40f).level(5);
	}
}
