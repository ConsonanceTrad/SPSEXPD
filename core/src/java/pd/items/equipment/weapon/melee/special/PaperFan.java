/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Vertigo;
import pd.items.equipment.wands.fusion.WandOfFlow;
import pd.mechanics.Ballistica;
import render.utils.serialize.Bundle;

public class PaperFan extends SpsSpecialMeleeWeapon {
	private static final String CHARGE = "charge";
	private int charge;

	public PaperFan() { super(2, 1f, 1f, 2, 1, 15, SpecificPlaceHolderDict.SOMETHING_0); }

	@Override public int proc(Char attacker, Char defender, int damage) {
		charge++;
		if (charge > 8) {
			if (Dungeon.level != null) {
				int opposite = defender.pos + defender.pos - attacker.pos;
				if (Dungeon.level.insideMap(opposite)) {
					WandOfFlow.throwChar(defender,
							new Ballistica(defender.pos, opposite, Ballistica.MAGIC_BOLT), 1);
				}
			}
			Buff.prolong(defender, Vertigo.class, 3f);
			charge = 0;
		}
		return super.proc(attacker, defender, damage);
	}

	public int charge() { return charge; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = bundle.getInt(CHARGE); }
}
