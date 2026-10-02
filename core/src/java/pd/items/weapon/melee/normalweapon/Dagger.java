package pd.items.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;

public class Dagger extends NormalMeleeWeapon {
	public Dagger() { super(1, 1f, 1f, 1, 1, 10, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { if (s.accuracy < 4f) s.accuracy += .2f; s.min++; s.max++; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int roll = attackerRoll(attacker);
		defender.damage(safeRandom(roll / 2, roll * 3 / 4), this);
		return super.proc(attacker, defender, damage);
	}
}
