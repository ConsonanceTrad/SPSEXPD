package pd.items.equipment.weapon.missiles.fusion;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.items.equipment.weapon.missiles.HeavyBoomerang;
import render.utils.math.Random;

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
