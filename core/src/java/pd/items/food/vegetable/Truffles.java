package pd.items.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import render.utils.math.Random;

public class Truffles extends Vegetable {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onEat(Hero hero) {
		hero.HTBoost += Random.IntRange(1, 2);
		hero.updateHT(true);
	}
	@Override public int value() { return 250 * quantity; }
}
