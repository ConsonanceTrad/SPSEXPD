/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package pd.items.weapon.missiles.arrows;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.items.Heap;
import pd.items.Item;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

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
