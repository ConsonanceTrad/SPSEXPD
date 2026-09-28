/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class IceMeat extends MeatFood {
	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0044FF);
	{
		image = ItemSpriteSheet.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { IceMeat result = new IceMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, Invisibility.class, 20f);
	}
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
	@Override public int value() { return 3 * quantity; }
}
