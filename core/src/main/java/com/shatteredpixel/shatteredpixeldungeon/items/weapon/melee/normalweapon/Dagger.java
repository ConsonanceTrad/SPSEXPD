package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Dagger extends NormalMeleeWeapon {
	public Dagger() { super(1, 1f, 1f, 1, 1, 10, ItemSpriteSheet.SPS_WEP_DAGGER); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.accuracy < 4f) s.accuracy += .2f; s.min++; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int roll = attackerRoll(attacker);
		defender.damage(safeRandom(roll / 2, roll * 3 / 4), this);
		return super.proc(attacker, defender, damage);
	}
}
