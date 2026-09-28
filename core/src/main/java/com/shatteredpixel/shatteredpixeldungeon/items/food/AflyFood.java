/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AflyBless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

/** The original Fushigi-no rice ball made by Alfred. */
public class AflyFood extends Food {
	{
		image = ItemSpriteSheet.AFLY_FOOD;
		energy = 200f;
	}
	@Override protected void satisfy(Hero hero) {
		super.satisfy(hero);
		Buff.affect(hero, AflyBless.class, 150f);
	}
	@Override public int value() { return 2 * quantity; }
}
