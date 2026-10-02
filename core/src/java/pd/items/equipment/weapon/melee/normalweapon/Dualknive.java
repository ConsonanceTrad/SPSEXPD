package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;

public class Dualknive extends NormalMeleeWeapon {
	public Dualknive() { super(2, 1f, 1f, 1, 11, 17, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.accuracy < 1.2f) s.accuracy += .05f;
		if (s.accuracy > 1.2f && s.delay > .8f) s.delay -= .05f;
		if (s.delay < .8f && s.reach < 2) s.reach++;
		s.min++; s.max++;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		int roll = attackerRoll(attacker);
		defender.damage(safeRandom(roll / 4, roll / 2), this);
		return super.proc(attacker, defender, damage);
	}
}
