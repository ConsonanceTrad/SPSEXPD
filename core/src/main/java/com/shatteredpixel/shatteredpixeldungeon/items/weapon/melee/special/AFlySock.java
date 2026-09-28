/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

/** AFly's tier-one sock, applying one of four control effects on every hit. */
public class AFlySock extends MeleeWeapon {

	{
		image = ItemSpriteSheet.AFLY_SOCK;
		tier = 1;
	}

	@Override public int min(int lvl) { return 1 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 5 + Math.max(0, lvl); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		applyEffect(attacker, defender, Random.Int(4));
		return super.proc(attacker, defender, damage);
	}

	void applyEffect(Char attacker, Char defender, int result) {
		switch (result) {
			case 0: Buff.affect(defender, Paralysis.class, 3f); break;
			case 1: Buff.affect(defender, Charm.class, 3f).object = attacker.id(); break;
			case 2: Buff.affect(defender, Terror.class, 3f).object = attacker.id(); break;
			case 3: Buff.affect(defender, Amok.class, 3f); break;
			default: throw new IllegalArgumentException("Unknown AFlySock result: " + result);
		}
	}

	@Override public int value() { return legacyValue(); }

	private int legacyValue() {
		int result = enchantment == null ? 50 : 75;
		if (cursed && cursedKnown) result /= 2;
		if (levelKnown) {
			if (trueLevel() > 0) result *= trueLevel() + 1;
			else if (trueLevel() < 0) result /= 1 - trueLevel();
		}
		return Math.max(1, result);
	}
}
