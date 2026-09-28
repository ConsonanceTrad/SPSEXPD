/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014 Oleg Dolya
 *
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RiceBall extends Arrows {

	public static final float DURATION = 10f;

	{
		image = ItemSpriteSheet.RICE_BALL;
	}

	public RiceBall() { this(1); }

	public RiceBall(int number) {
		super(1, 1);
		quantity(number);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (canFeed(defender)) {
			Buff.affect(defender, Drowsy.class);
			if (defender.sprite != null) {
				defender.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
			}
			if (defender.HT > 0 && (float)defender.HP / defender.HT > 0.01f) {
				teleportTarget(defender);
			}
		}
		return super.proc(attacker, defender, damage);
	}

	static boolean canFeed(Char defender) {
		return defender != null
				&& !(defender instanceof NPC)
				&& !Char.hasProp(defender, Char.Property.UNDEAD)
				&& !Char.hasProp(defender, Char.Property.BOSS)
				&& !Char.hasProp(defender, Char.Property.DEMONIC)
				&& !Char.hasProp(defender, Char.Property.UNKNOW)
				&& !Char.hasProp(defender, Char.Property.ELEMENT)
				&& !Char.hasProp(defender, Char.Property.MECH);
	}

	static boolean teleportTarget(Char defender) {
		if (Dungeon.level == null || defender == null) return false;
		for (int attempt = 0; attempt < 20; attempt++) {
			int destination = Dungeon.level.randomRespawnCell(defender);
			if (!Dungeon.level.insideMap(destination)
					|| (!Dungeon.level.passable[destination] && !Dungeon.level.avoid[destination])
					|| Actor.findChar(destination) != null) continue;
			defender.pos = destination;
			Dungeon.level.occupyCell(defender);
			if (defender.sprite != null) {
				defender.sprite.place(destination);
				defender.sprite.visible = Dungeon.level.heroFOV[destination];
			}
			return true;
		}
		return false;
	}

	@Override public int value() { return quantity() * 10; }
}
