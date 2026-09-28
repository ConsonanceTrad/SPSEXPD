/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food.completefood;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Notice;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class FruitCandy extends CompleteFood {

	{
		image = ItemSpriteSheet.FRUIT_CANDY;
		energy = 20f;
	}

	public FruitCandy() { this(2); }
	public FruitCandy(int number) { quantity = number; }

	@Override
	protected void doEat(Hero hero) {
		switch (Random.Int(3)) {
			case 0:
				Buff.affect(hero, HasteBuff.class, 20f);
				Buff.affect(hero, Levitation.class, 20f);
				break;
			case 1:
				Buff.affect(hero, Notice.class, Notice.DURATION);
				break;
			default:
				Buff.affect(hero, BerryRegeneration.class).level(Math.max(hero.HT / 10, 10));
				break;
		}
	}

	@Override public Item random() { quantity = Random.Int(2, 4); return this; }
	@Override public int value() { return 10 * quantity; }
}
