/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Chocolate extends CompleteFood {
	{ image = ItemSpriteSheet.CHOCOLATE; energy = 300f; }
	@Override protected void doEat(Hero hero) { Buff.affect(hero, ShieldArmor.class).level(hero.HT); }
	@Override public int value() { return 60 * quantity; }
}
