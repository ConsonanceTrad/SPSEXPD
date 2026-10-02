/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.specialarmor;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipArmorUniqueArmorDict;

/** Living armor which stores recent damage as defense, then converts it to healing. */
public class LifeArmor extends NormalArmor {
	{
		image = EquipmentEquipArmorUniqueArmorDict.LIVING_ARMOR;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LifeArmor.class)
			.t("name", "活性护甲")
			.t("desc", "一株被塑造成护甲的活体植物。它会积蓄近期受到的伤害，并调整厚度以格挡至多等量伤害；20回合没有受到新伤害后，会把积蓄值转化为治疗。");
	}



	private static final String CHARGE = "charge";
	private static final String TIME = "time";
	private static final String ADAPTIVE_MAX = "adaptive_max";

	private int charge;
	private int time;
	private int adaptiveMax;
	private LifeCharge passiveBuff;

	public LifeArmor() {
		super(1, 2f, 6f, 3, 0, 0, 0, 0, 0, EquipmentNonEquipDict.SPS_LIFE_ARMOR_0);
	}

	@Override public int DRMin(int level) { return 0; }
	@Override public int DRMax(int level) { return Math.max(0, adaptiveMax); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		charge += Math.max(0, damage);
		time = 20;
		return super.proc(attacker, defender, damage);
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (passiveBuff != null && passiveBuff.target != null) passiveBuff.detach();
		passiveBuff = new LifeCharge();
		passiveBuff.attachTo(ch);
	}

	@Override
	public void deactivate(Char ch) {
		super.deactivate(ch);
		if (passiveBuff != null && passiveBuff.target != null) passiveBuff.detach();
		passiveBuff = null;
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (!super.doUnequip(hero, collect, single)) return false;
		if (passiveBuff != null && passiveBuff.target != null) passiveBuff.detach();
		passiveBuff = null;
		charge = 0;
		time = 0;
		adaptiveMax = 0;
		return true;
	}

	public int charge() { return charge; }
	public int recoveryTime() { return time; }
	public int adaptiveMax() { return adaptiveMax; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
		bundle.put(TIME, time);
		bundle.put(ADAPTIVE_MAX, adaptiveMax);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, bundle.getInt(CHARGE));
		time = Math.max(0, bundle.getInt(TIME));
		adaptiveMax = Math.max(0, bundle.getInt(ADAPTIVE_MAX));
	}

	public class LifeCharge extends Buff {
		@Override
		public boolean act() {
			if (time > 1) {
				time--;
			} else {
				if (target != null) target.HP += Math.min(target.HT - target.HP, charge);
				charge = 0;
				time = 0;
			}
			adaptiveMax = charge >= adaptiveMax ? charge : 0;
			spend(TICK);
			return true;
		}

		public void tickNow() { act(); }
	}
}
