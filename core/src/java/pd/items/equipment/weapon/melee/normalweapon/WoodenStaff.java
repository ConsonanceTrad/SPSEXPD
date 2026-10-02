/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Char;
import pd.messages.Messages;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class WoodenStaff extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WoodenStaff.class)
			.t("name", "木杖")
			.t("charge", "积蓄：%1$d / %2$d。积蓄充满后，下一次攻击造成五倍伤害。")
			.t("desc", "用特殊木头雕刻而成的棍子。每次命中都会积蓄力量；命中八次后，下一击造成五倍伤害。");
	}


	private static final String CHARGE = "charge";
	private static final int FULL_CHARGE = 8;
	private int charge;

	public WoodenStaff() {
		super(1, .8f, 1f, 1, 1, 10, EquipmentEquipWeaponBasicWeaponDict.LEGACY_WOODEN_STAFF_0);
	}

	@Override
	protected void applyLegacyUpgrade(Stats stats) {
		if (stats.accuracy < 1.2f) stats.accuracy += .05f;
		if (stats.accuracy > 1.2f && stats.delay > .8f) stats.delay -= .05f;
	}

	@Override
	public int damageRoll(Char owner) {
		int damage = super.damageRoll(owner);
		return charge >= FULL_CHARGE ? damage * 5 : damage;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (charge >= FULL_CHARGE) charge = 0;
		charge++;
		updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	public int charge() { return charge; }

	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }
}
