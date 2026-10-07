package pd.items.consum.food.vegetable;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;

import pd.actors.buffs.Hunger;
import pd.messages.InlineText;

public class BrewLeft extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BrewLeft.class)
			.t("name", "酿造残渣")
			.t("desc", "酿造后留下的可食用残渣。营养不多，但浪费食物更加可惜。");
	}



	{ image = ConsumPotionSeedBasicPotionDict.BREW_LEFT; energy = 30f; /*SPSEXPD: 原 Hunger.HUNGRY / 10f*/ hornValue = 0; }
}
