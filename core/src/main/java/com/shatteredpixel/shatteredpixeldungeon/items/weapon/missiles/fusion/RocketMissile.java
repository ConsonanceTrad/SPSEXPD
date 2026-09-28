package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.fusion;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin;
import com.watabou.utils.Random;

public class RocketMissile extends Javelin {

	@Override
	public int max(int lvl) {
		return Math.max(min(lvl), super.max(lvl) - 3 - lvl);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(4) == 0) Buff.affect(defender, Burning.class).reignite(defender, 3f);
		return super.proc(attacker, defender, damage);
	}
}
