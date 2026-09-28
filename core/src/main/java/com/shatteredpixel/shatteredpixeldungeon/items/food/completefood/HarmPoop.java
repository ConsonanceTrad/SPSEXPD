/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class HarmPoop extends MeatFood {
	{
		image = ItemSpriteSheet.MEAT;
		energy = 10f;
		hornValue = 0;
	}
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, Poison.class).set(hero.HT / 10f);
		Buff.prolong(hero, Slow.class, 5f);
	}
	@Override public int value() { return 2 * quantity; }
}
