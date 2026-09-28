/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Icecream extends CompleteFood {
	{ image = ItemSpriteSheet.ICECREAM; energy = 90f; }
	@Override protected void doEat(Hero hero) {
		increaseMaxHealth(hero, 3, 6);
		heal(hero, (hero.HT - hero.HP) / 2);
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Burning.class);
		Buff.detach(hero, STRDown.class);
	}
	@Override public int value() { return 300 * quantity; }
}
