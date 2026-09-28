/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Honeymeat extends CompleteFood {
	{ image = ItemSpriteSheet.HONEY_MEAT; energy = 150f; }
	@Override protected void doEat(Hero hero) {
		increaseMaxHealth(hero, 3, 6);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}
	@Override public int value() { return 400 * quantity; }
}
