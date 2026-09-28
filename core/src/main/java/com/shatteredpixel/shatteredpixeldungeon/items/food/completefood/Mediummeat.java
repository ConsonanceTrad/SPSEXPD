/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Mediummeat extends CompleteFood {
	{
		image = ItemSpriteSheet.STEAK;
		energy = 180f;
	}
	@Override protected void doEat(Hero hero) { Buff.affect(hero, AttackUp.class, 50f).level(60); }
	@Override public int value() { return 3 * quantity; }
}
