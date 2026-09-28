/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Fruitsalad extends CompleteFood {
	{ image = ItemSpriteSheet.FRUIT_SALAD; energy = 130f; }
	@Override protected void doEat(Hero hero) {
		heal(hero, hero.HT / 3);
		Buff.affect(hero, BerryRegeneration.class).level(Math.max(hero.HT / 2, 30));
	}
	@Override public int value() { return 2 * quantity; }
}
