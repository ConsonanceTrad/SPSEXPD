/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PerfectFood extends CompleteFood {
	{ image = ItemSpriteSheet.PERFECT_FOOD; energy = 600f; }
	@Override protected void doEat(Hero hero) {
		increaseMaxHealth(hero, 3, 7);
		Buff.affect(hero, Bless.class, 50f);
		Buff.affect(hero, Light.class, 50f);
		Buff.affect(hero, HasteBuff.class, 25f);
		Buff.affect(hero, Levitation.class, 25f);
	}
	@Override public int value() { return 50 * quantity; }
}
