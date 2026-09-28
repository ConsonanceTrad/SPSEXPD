/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class DarkMeat extends MeatFood {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	{
		image = ItemSpriteSheet.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { DarkMeat result = new DarkMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		hero.HP = Math.min(hero.HT, hero.HP + hero.HT / 4);
	}
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
	@Override public int value() { return 3 * quantity; }
}
