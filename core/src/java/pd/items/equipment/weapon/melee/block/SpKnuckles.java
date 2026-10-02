/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.block;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.ShieldArmor;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import render.utils.math.Random;

/** The knuckle-sect weapon sold by Shower after Otiluke is rescued. */
public class SpKnuckles extends NormalMeleeWeapon {

	public SpKnuckles() {
		super(1, 2f, 0.5f, 2, 1, 10, SpecificPlaceHolderDict.SOMETHING_0);
	}

	@Override
	protected void applyLegacyUpgrade(Stats stats) {
		stats.min++;
		stats.max += 2;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 50) Buff.prolong(defender, Paralysis.class, 2f);
		if (attacker.buff(ShieldArmor.class) == null) {
			Buff.affect(attacker, ShieldArmor.class).level(attacker.HT / 10);
		}
		return super.proc(attacker, defender, damage);
	}
}
