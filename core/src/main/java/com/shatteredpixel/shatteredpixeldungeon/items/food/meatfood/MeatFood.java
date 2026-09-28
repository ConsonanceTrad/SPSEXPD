/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MeatFood extends Food {
	{
		stackable = true;
		image = ItemSpriteSheet.MEAT;
		hornValue = 1;
	}
	@Override protected void satisfy(Hero hero) {
		super.satisfy(hero);
		doEat(hero);
	}
	protected void doEat(Hero hero) { }
}
