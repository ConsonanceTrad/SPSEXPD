package com.shatteredpixel.shatteredpixeldungeon.items.armor.fusion;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.watabou.utils.Random;

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
