/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Vertigo;
import pd.items.equipment.wands.fusion.WandOfFlow;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class NinjaFan extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NinjaFan.class)
			.t("name", "忍者蒲扇")
			.t("desc", "能够施展强风的巨型忍者蒲扇。每第七次命中会击退目标并使其眩晕。")
			.t("charge", "蓄风：_%1$d/%2$d_。");
	}



	private static final String CHARGE = "charge";
	private int charge;

	public NinjaFan() { super(1, 1f, 1f, 2, 1, 10, EquipmentEquipWeaponBasicWeaponDict.SPS_NINJA_FAN_0); }

	@Override public int proc(Char attacker, Char defender, int damage) {
		charge++;
		if (charge > 6) {
			if (Dungeon.level != null) {
				int opposite = defender.pos + defender.pos - attacker.pos;
				WandOfFlow.throwChar(defender,
						new Ballistica(defender.pos, opposite, Ballistica.MAGIC_BOLT), 2);
			}
			Buff.prolong(defender, Vertigo.class, 3f);
			charge = 0;
		}
		updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	public int charge() { return charge; }
	@Override public String status() { return charge + "/6"; }
	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "charge", charge, 6); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(6, bundle.getInt(CHARGE))); }
}
