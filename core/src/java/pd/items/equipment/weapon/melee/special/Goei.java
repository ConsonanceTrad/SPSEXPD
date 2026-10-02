/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import render.utils.serialize.Bundle;

public class Goei extends SpsSpecialMeleeWeapon {
	private static final String CHARGE = "charge";
	private int charge;

	public Goei() { super(3, 1f, 1f, 2, 4, 15, SpecificPlaceHolderDict.SOMETHING_0); }

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (charge >= 5) {
			defender.damage(Math.max(0, damage), this);
			charge = 0;
		}
		if (defender.properties().contains(Char.Property.DEMONIC)
				|| defender.properties().contains(Char.Property.UNDEAD)) {
			defender.damage(Math.max(0, (int)(damage * .35f)), this);
		}
		charge++;
		return super.proc(attacker, defender, damage);
	}

	public int charge() { return charge; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = bundle.getInt(CHARGE); }
}
