/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Meat extends MeatFood {
	{
		image = ItemSpriteSheet.MEAT;
		energy = 100f;
	}
	@Override protected void doEat(Hero hero) {
		if (Random.Int(15) == 0) Buff.affect(hero, Poison.class).set(hero.HT / 5f);
	}
	@Override public int value() { return 2 * quantity; }
}
