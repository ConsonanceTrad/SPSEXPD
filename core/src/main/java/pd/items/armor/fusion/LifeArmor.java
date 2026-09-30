package pd.items.armor.fusion;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.armor.ScaleArmor;
import render.utils.math.Random;

public class LifeArmor extends ScaleArmor {

	@Override
	public int DRMax(int lvl) {
		return Math.max(0, super.DRMax(lvl) - 1);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);
		if (damage > 0 && defender == Dungeon.hero && Dungeon.hero.HP < Dungeon.hero.HT
				&& Random.Int(12) == 0) {
			Dungeon.hero.HP++;
		}
		return damage;
	}
}
