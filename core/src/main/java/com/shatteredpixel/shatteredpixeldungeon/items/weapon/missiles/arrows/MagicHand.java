/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class MagicHand extends Arrows {

	{
		image = ItemSpriteSheet.MAGIC_HAND;
	}

	public MagicHand() { this(1); }

	public MagicHand(int number) {
		super(1, 5);
		quantity(number);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		if (!(defender instanceof Mob) || !((Mob)defender).firstItem) return result;

		Mob mob = (Mob)defender;
		mob.firstItem = false;
		Item loot = mob.SupercreateLoot();
		if (loot != null && Dungeon.level != null && attacker != null
				&& Dungeon.level.insideMap(attacker.pos)) {
			Heap heap = Dungeon.level.drop(loot, attacker.pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}
		return result;
	}

	@Override public MagicHand random() { quantity(Random.Int(3, 5)); return this; }
	@Override public int value() { return quantity() * 20; }
}
