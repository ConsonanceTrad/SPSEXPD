package pd.items.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import render.utils.math.Random;

public class ShortSword extends NormalMeleeWeapon {
	public ShortSword() { super(1, 1f, 1f, 1, 1, 10, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) { s.min += 3; s.max += 3; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 50) Buff.affect(defender, Bleeding.class).set(safeRandom(1, damage));
		return super.proc(attacker, defender, damage);
	}
}
