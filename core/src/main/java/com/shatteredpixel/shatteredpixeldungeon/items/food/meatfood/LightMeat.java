/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LightMeat extends MeatFood {
	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	{
		image = ItemSpriteSheet.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { LightMeat result = new LightMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		if (Dungeon.level != null) Dungeon.level.drop(new SmallMeat(), hero.pos);
	}
	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
	@Override public int value() { return 3 * quantity; }
}
