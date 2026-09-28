/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Hamburger extends CompleteFood {
	{ image = ItemSpriteSheet.HAMBURGER; energy = 770f; }
	@Override protected void doEat(Hero hero) {
		heal(hero, hero.HT / 5);
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 3);
		Buff.affect(hero, AttackUp.class, 50f).level(70);
	}
	@Override public int value() { return 10 * quantity; }
}
