package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.fusion;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.watabou.utils.Random;

public class TempestBoomerang extends HeavyBoomerang {

	@Override
	public int max(int lvl) {
		return Math.max(min(lvl), super.max(lvl) - 2);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(4) == 0) Buff.prolong(defender, Cripple.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}
