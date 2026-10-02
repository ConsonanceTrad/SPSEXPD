/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Char;
import pd.items.equipment.weapon.melee.Mace;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class PrayerWheel extends Mace implements FusionWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PrayerWheel.class)
			.t("name", "转经轮")
			.t("desc", "一件稳重的四阶武器，会在攻击中积蓄动能。每第八次攻击造成50%额外伤害，随后清空蓄力。");
	}



	private int charge;
	{ image = EquipmentEquipWeaponBasicWeaponDict.PRAYER_WHEEL; tier = 4; ACC = 0.9f; }
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
