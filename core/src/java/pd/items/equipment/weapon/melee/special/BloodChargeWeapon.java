/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/**
 * SPSEXPD: 血源风「猎魂武器」基类（移植自血源诅咒地牢 1.8.2 的 HunterGaunlet / NecroBlade）。
 *
 * 武器积攒「血源之力」（击杀获得，容量 100），储存的能量提供额外伤害；
 * 蓄满后可用「强化」动作消耗 100 点能量升级 1 级——不限次数，可以无限提升。
 */
public abstract class BloodChargeWeapon extends MeleeWeapon {

	public static final String AC_BLOOD_UPGRADE = "BLOOD_UPGRADE";
	public static final int FULL_BLOOD_CHARGE = 100;

	/** 当前储存的血源之力（0~100）。 */
	public int bloodCharge = FULL_BLOOD_CHARGE;

	/** 储存能量按该除数折算成额外伤害上限（0 - charge/divisor）。 */
	protected abstract int damagePerCharge();

	/** 击杀积攒能量（血源规则：敌人 HT/25，不足 25 按 1）。 */
	public void gainBloodCharge(Mob victim) {
		if (victim == null) return;
		int gain = victim.HT > 25 ? Math.max(1, victim.HT / 25) : 1;
		updateBloodCharge(gain);
	}

	public void updateBloodCharge(int change) {
		bloodCharge = Math.max(0, Math.min(FULL_BLOOD_CHARGE, bloodCharge + change));
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (bloodCharge >= FULL_BLOOD_CHARGE) actions.add(AC_BLOOD_UPGRADE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_BLOOD_UPGRADE.equals(action)) {
			if (bloodCharge < FULL_BLOOD_CHARGE) return;
			updateBloodCharge(-FULL_BLOOD_CHARGE);
			upgrade(1);
			GLog.p(Messages.get(this, "blood_upgrade"));
			GLog.i(Messages.get(this, "blood_whisper"));
		} else {
			super.execute(hero, action);
		}
	}

	/** 储存能量提供的随机额外伤害。 */
	public int bloodBonusDamage() {
		int per = damagePerCharge();
		return per <= 0 ? 0 : render.utils.math.Random.Int(bloodCharge / per);
	}

	private static final String BLOOD_CHARGE = "blood_charge";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BLOOD_CHARGE, bloodCharge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		bloodCharge = bundle.contains(BLOOD_CHARGE) ? bundle.getInt(BLOOD_CHARGE) : FULL_BLOOD_CHARGE;
	}
}
