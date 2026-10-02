package pd.items.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import render.utils.math.Random;

public class Scimitar extends NormalMeleeWeapon {
	public Scimitar() { super(3, 1f, 1f, 1, 23, 35, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.accuracy < 1.5f) s.accuracy += .025f;
		if (s.delay > .8f) s.delay -= .05f;
		s.min++; s.max += 4;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30) {
			Buff.affect(defender, Bleeding.class).set(safeRandom(3, damage));
			Buff.affect(defender, ArmorBreak.class, 5f).level(30);
		}
		return super.proc(attacker, defender, damage);
	}
}
