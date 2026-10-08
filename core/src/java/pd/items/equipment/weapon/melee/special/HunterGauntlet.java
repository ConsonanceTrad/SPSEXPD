/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.messages.InlineText;
import pd.messages.Messages;

/**
 * SPSEXPD: 血源风猎魂武器——猎魂拳套（参考 HunterGaunlet）。
 * 击杀积攒血源之力；储存能量提供 0 - charge/10 的额外伤害；满能量可无限提升。
 */
public class HunterGauntlet extends BloodChargeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HunterGauntlet.class)
			.t("name", "猎魂拳套")
			.t("desc", "带锋利尖刺的钢制拳套。很久以前猎人们用它在夜色中偷袭野兽。猎魂武器会吸收死敌的血源之力增强自己。\n\n当前血源之力：%1$d/100，额外伤害 0~%2$d。\n蓄满后可用「强化」消耗全部能量提升 1 级，不限次数。")
			.t("blood_upgrade", "拳套消耗了储存的血源之力，变得明显更好了。")
			.t("blood_whisper", "你听到一些微弱的耳语……");
	}

	{
		image = EquipmentEquipWeaponBasicWeaponDict.GAUNTLETS_0;
		tier = 3;
		DLY = 0.5f;
		bones = false;
	}

	@Override protected int damagePerCharge() { return 10; }

	@Override public int min(int lvl) { return Math.round((tier + 1) * 2.5f) + Math.round((tier + 1) * 0.5f) * lvl; }
	@Override public int max(int lvl) { return Math.round((tier + 1) * 5f) + Math.round((tier + 1) * 0.75f) * lvl; }

	@Override
	public int damageRoll(Char owner) {
		return super.damageRoll(owner) + bloodBonusDamage();
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Bleeding.class).set(Math.max(1, Math.round(damage / 4f)));
		return super.proc(attacker, defender, Math.round(damage / 1.2f));
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", bloodCharge, bloodCharge / damagePerCharge());
	}
}
