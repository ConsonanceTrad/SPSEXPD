/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Sishimi extends CompleteFood {
	{
		image = ItemSpriteSheet.MEAT;
		energy = 180f;
	}
	@Override protected void doEat(Hero hero) { Buff.affect(hero, MagicArmor.class).level(hero.HT / 5); }
	@Override public int value() { return 3 * quantity; }
}
