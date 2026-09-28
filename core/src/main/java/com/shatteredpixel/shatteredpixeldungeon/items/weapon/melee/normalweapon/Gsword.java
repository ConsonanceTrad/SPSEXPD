package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Gsword extends NormalMeleeWeapon {
	public Gsword() { super(5, 1f, 1f, 1, 50, 64, ItemSpriteSheet.SPS_WEP_GSWORD); }
	@Override protected void applyLegacyUpgrade(Stats s) { s.min += 3; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 10) Buff.affect(defender, Bleeding.class).set(safeRandom(5, damage));
		return super.proc(attacker, defender, damage);
	}
}
