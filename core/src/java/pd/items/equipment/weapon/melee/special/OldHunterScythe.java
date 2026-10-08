/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 血源风猎魂武器——老猎人的镰刀（参考 NecroBlade）。
 * 击杀积攒血源之力；储存能量提供 0 - charge/8 的额外伤害；
 * 可消耗 25 点能量治疗自身，蓄满后可无限提升。
 */
public class OldHunterScythe extends BloodChargeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(OldHunterScythe.class)
			.t("name", "老猎人的镰刀")
			.t("desc", "一把很久以前第一批猎人锻造的奥术大镰。它会吸取所收割生命的血源之力。\n\n当前血源之力：%1$d/100，额外伤害 0~%2$d。\n可用「收割」消耗 25 点能量治疗自己；蓄满后可用「强化」提升 1 级，不限次数。")
			.t("ac_reap", "收割")
			.t("reap_done", "镰刀治疗了你 %1$d 点生命。")
			.t("blood_upgrade", "镰刀消耗了储存的血源之力，看起来明显更强了。")
			.t("blood_whisper", "你听到一些可怕的耳语……");
	}

	public static final String AC_REAP = "REAP";
	public static final int REAP_COST = 25;

	{
		image = EquipmentEquipWeaponBasicWeaponDict.WAR_SCYTHE_0;
		tier = 4;
		RCH = 2;
		bones = false;
	}

	@Override protected int damagePerCharge() { return 8; }

	@Override public int min(int lvl) { return Math.round((tier + 1) * 3f) + Math.round((tier + 1) * 0.8f) * lvl; }
	@Override public int max(int lvl) { return 18 + tier * lvl; }

	@Override
	public int damageRoll(Char owner) {
		return super.damageRoll(owner) + bloodBonusDamage();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (bloodCharge > REAP_COST) actions.add(AC_REAP);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_REAP.equals(action)) {
			if (bloodCharge <= REAP_COST || hero == null) return;
			updateBloodCharge(-REAP_COST);
			int heal = Math.max(1, Math.round(hero.HT * 0.35f));
			hero.HP = Math.min(hero.HT, hero.HP + heal);
			GLog.p(Messages.get(this, "reap_done", heal));
			if (Dungeon.level != null && hero.sprite != null) {
				CellEmitter.center(hero.pos).burst(Speck.factory(Speck.HEALING), 1);
			}
		} else {
			super.execute(hero, action);
		}
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", bloodCharge, bloodCharge / damagePerCharge());
	}
}
