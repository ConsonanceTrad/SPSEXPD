package pd.items.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import render.utils.math.Random;

public class AssassinsBlade extends NormalMeleeWeapon {
	public AssassinsBlade() { super(4, 1f, 1f, 1, 26, 34, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { s.min += 3; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 50) {
			int roll = attackerRoll(attacker);
			defender.damage(safeRandom(roll / 4, roll / 2), this);
		}
		return super.proc(attacker, defender, damage);
	}
}
