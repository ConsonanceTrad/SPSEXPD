package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import render.utils.math.Random;

public class BattleAxe extends NormalMeleeWeapon {
	public BattleAxe() { super(4, 1f, 1f, 1, 36, 49, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override protected void applyLegacyUpgrade(Stats s) {
		if (s.accuracy < 1.2f) s.accuracy += .05f;
		if (s.accuracy > 1.2f && s.delay > .9f) s.delay -= .05f;
		s.max += 6;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 20) Buff.affect(defender, Bleeding.class).set(safeRandom(4, damage));
		return super.proc(attacker, defender, damage);
	}
}
