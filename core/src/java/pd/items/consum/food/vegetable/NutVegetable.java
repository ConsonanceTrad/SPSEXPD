package pd.items.consum.food.vegetable;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class NutVegetable extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NutVegetable.class)
			.t("name", "坚果藤")
			.t("desc", "一种常用于烹饪的蔬菜。即使生食也很可口，并能提供少量护盾。");
	}

	{ image = ConsumPotionSeedSeedDict.NUT_VEGETABLE; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.HT / 5));
	}
}
