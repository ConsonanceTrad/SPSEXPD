package pd.items.consum.food.vegetable;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.hero.Hero;
import pd.items.consum.potions.PotionOfHealing;
import pd.messages.InlineText;

public class DreamLeaf extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DreamLeaf.class)
			.t("name", "好梦叶")
			.t("desc", "夜梦草的一部分，可以食用。它能清除中毒等常见负面状态。");
	}



	{ image = ConsumPotionSeedSeedDict.DREAM_LEAF; }
	@Override protected void onEat(Hero hero) {
		PotionOfHealing.cure(hero);
	}
}
