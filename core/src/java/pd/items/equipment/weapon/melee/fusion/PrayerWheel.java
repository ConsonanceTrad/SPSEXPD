/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Char;
import pd.items.equipment.weapon.melee.Mace;
import render.utils.serialize.Bundle;

public class PrayerWheel extends Mace implements FusionWeapon {
	private int charge;
	{ image = EquipmentEquipWeaponBasicWeaponDict.ROUND_SHIELD_0; tier = 4; ACC = 0.9f; }
	@Override public int min(int lvl) { return 6 + lvl; }
	@Override public int max(int lvl) { return 23 + 5 * lvl; }
	@Override public int damageRoll(Char owner) {
		int damage = super.damageRoll(owner);
		return charge >= 7 ? Math.round(damage * 1.5f) : damage;
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		charge = charge >= 7 ? 0 : charge + 1;
		return result;
	}
	@Override public String status() { return charge + "/8"; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put("charge", charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = bundle.getInt("charge"); }
}
