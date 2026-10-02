/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Char;
import pd.items.Item;
import pd.messages.Messages;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class HolyWater extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HolyWater.class)
			.t("name", "圣水")
			.t("desc", "被祝福过的水制成的奇特武器。每次命中都会积蓄力量；满充后的下一击造成五倍伤害，并按基础伤害恢复生命。")
			.t("charge", "充能：%1$d / %2$d。");
	}



	public static final int FULL_CHARGE = 14;
	private static final String CHARGE = "charge";
	private int charge;

	{
		image = EquipmentEquipWeaponBasicWeaponDict.HOLY_WATER;
		tier = 3;
		ACC = 0.8f;
		DLY = 1.2f;
		RCH = 1;
		levelKnown = true;
	}

	@Override public int min(int lvl) { return 22 + 2 * lvl; }
	@Override public int max(int lvl) { return 34 + 2 * lvl; }
	@Override public int STRReq(int lvl) { return 14; }
	@Override public boolean isUpgradable() { return true; }
	@Override public boolean isIdentified() { return true; }

	@Override
	public float accuracyFactor(Char owner, Char target) {
		float base = super.accuracyFactor(owner, target);
		float upgraded = Math.min(1.2f, 0.8f + 0.05f * Math.max(0, buffedLvl()));
		return base * upgraded / 0.8f;
	}

	@Override
	public int damageRoll(Char owner) {
		int damage = super.damageRoll(owner);
		return charge >= FULL_CHARGE ? damage * 5 : damage;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (charge >= FULL_CHARGE) {
			attacker.HP = Math.min(attacker.HT, attacker.HP + Math.max(0, damage / 5));
			charge = 0;
		}
		charge++;
		updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	public int charge() { return charge; }
	public void gainCharge(int amount) { charge = Math.max(0, charge + amount); updateQuickslot(); }
	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, bundle.getInt(CHARGE)); }
	@Override public Item upgrade(boolean enchant) { return super.upgrade(enchant); }
}
