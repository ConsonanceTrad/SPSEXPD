/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class EarthMeat extends MeatFood {
	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0x996600);
	{
		image = ItemSpriteSheet.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { EarthMeat result = new EarthMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, Barkskin.class).set(hero.lvl * 3, 1);
	}
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public int value() { return 3 * quantity; }
}
