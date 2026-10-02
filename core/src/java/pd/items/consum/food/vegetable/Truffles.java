package pd.items.consum.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Truffles extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Truffles.class)
			.t("name", "松露")
			.t("desc", "生长在地下的稀有甜味菌类。食用后可永久提高生命上限。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onEat(Hero hero) {
		hero.HTBoost += Random.IntRange(1, 2);
		hero.updateHT(true);
	}
	@Override public int value() { return 250 * quantity; }
}
