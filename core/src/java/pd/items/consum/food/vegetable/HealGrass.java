package pd.items.consum.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedSeedDict;

public class HealGrass extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HealGrass.class)
			.t("name", "治疗草")
			.t("desc", "阳春草的一部分，可以食用。它能恢复生命并提供暂时的奥术防护。");
	}



	{ image = ConsumPotionSeedSeedDict.SUNFLOWER; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Healing.class).setHeal(20, 0.25f, 0);
		Buff.affect(hero, ArcaneArmor.class).set(Math.max(1, hero.HT / 5), 20);
	}
}
