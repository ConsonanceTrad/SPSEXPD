package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HolyStun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Nunchakus extends NormalMeleeWeapon {
	public Nunchakus() { super(3, 1f, 1f, 1, 18, 27, ItemSpriteSheet.SPS_WEP_NUNCHAKUS); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.delay > .75f) s.delay -= .05f; s.min += 2; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 20) Buff.prolong(defender, HolyStun.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
