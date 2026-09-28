package com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Truffles extends Vegetable {
	{ image = ItemSpriteSheet.TRUFFLES; }
	@Override protected void onEat(Hero hero) {
		hero.HTBoost += Random.IntRange(1, 2);
		hero.updateHT(true);
	}
	@Override public int value() { return 250 * quantity; }
}
