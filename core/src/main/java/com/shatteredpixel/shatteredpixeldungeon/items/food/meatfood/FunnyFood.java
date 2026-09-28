/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FunnyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class FunnyFood extends MeatFood {
	{
		image = ItemSpriteSheet.BUG_MEAT;
		energy = 500f;
	}
	@Override protected void doEat(Hero hero) {
		Buff.prolong(hero, FunnyBuff.class, 1600f);
	}
	@Override public int value() { return quantity; }
}
