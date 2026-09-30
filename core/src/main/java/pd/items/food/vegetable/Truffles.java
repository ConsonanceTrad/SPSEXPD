package pd.items.food.vegetable;

import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

public class Truffles extends Vegetable {
	{ image = ItemSpriteSheet.TRUFFLES; }
	@Override protected void onEat(Hero hero) {
		hero.HTBoost += Random.IntRange(1, 2);
		hero.updateHT(true);
	}
	@Override public int value() { return 250 * quantity; }
}
