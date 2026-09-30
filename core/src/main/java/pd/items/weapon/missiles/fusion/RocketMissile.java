package pd.items.weapon.missiles.fusion;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.items.weapon.missiles.Javelin;
import watabou.utils.Random;

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
