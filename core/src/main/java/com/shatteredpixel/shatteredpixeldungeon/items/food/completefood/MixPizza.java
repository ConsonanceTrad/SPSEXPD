/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class MixPizza extends CompleteFood {

	{
		image = ItemSpriteSheet.MIX_PIZZA;
		energy = 50f;
	}

	public MixPizza() { this(4); }
	public MixPizza(int number) { quantity = number; }

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, Bless.class, 10f);
		Buff.affect(hero, Light.class, 10f);
		Buff.affect(hero, HasteBuff.class, 10f);
		Buff.affect(hero, Levitation.class, 10f);
	}

	@Override public Item random() { quantity = Random.Int(3, 6); return this; }
	@Override public int value() { return 10 * quantity; }
}
