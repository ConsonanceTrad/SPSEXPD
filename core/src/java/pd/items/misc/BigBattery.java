/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BeTired;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.HighLight;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Rhythm;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndUseItem;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentNonEquipDict;

public class BigBattery extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BigBattery.class)
			.t("name", "蓄电池")
			.t("ac_use", "充能")
			.t("ac_add", "过载")
			.t("break", "蓄电池的能量不足。")
			.t("charge", "能量：%1$d / %2$d。")
			.t("desc", "一块活着的电池，会缓慢收集静电。它可以强化使用者并为法杖充能，也可以使视野内的所有敌人陷入疲劳。");
	}




	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_USE = "USE";
	public static final String AC_ADD = "ADD";
	public static final int FULL_CHARGE = 20;
	public static final int USE_COST = 15;
	private static final String CHARGE = "charge";

	private int charge;

	{
		image = EquipmentNonEquipDict.BATTERY;
		unique = true;
		defaultAction = AC_CHOOSE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_USE);
		actions.add(AC_ADD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) {
			GameScene.show(new WndUseItem(null, this));
		} else if (AC_USE.equals(action)) {
			if (!consumeCharge()) {
				GLog.i(Messages.get(this, "break"));
				return;
			}
			empower(hero);
			hero.spendAndNext(1f);
		} else if (AC_ADD.equals(action)) {
			if (!consumeCharge()) {
				GLog.i(Messages.get(this, "break"));
				return;
			}
			if (Dungeon.level != null) {
				for (Mob mob : Dungeon.level.mobs()) {
					if (mob.isAlive() && hero.fieldOfView != null && hero.fieldOfView[mob.pos]) {
						Buff.affect(mob, BeTired.class).set(30f);
					}
				}
			}
			hero.spendAndNext(1f);
		} else {
			super.execute(hero, action);
		}
	}

	public void gainCharge() {
		if (charge < FULL_CHARGE) {
			charge++;
			updateQuickslot();
		}
	}

	public void empower(Hero hero) {
		Buff.prolong(hero, Recharging.class, 25f);
		Buff.prolong(hero, Arcane.class, 5f);
		Buff.prolong(hero, HighLight.class, 25f);
		Buff.prolong(hero, AttackUp.class, 25f).level(30);
		Buff.prolong(hero, DefenceUp.class, 25f).level(30);
		Buff.prolong(hero, HasteBuff.class, 25f);
		Buff.prolong(hero, Rhythm.class, 25f);
	}

	public int charge() { return charge; }
	public boolean consumeCharge() {
		if (charge < USE_COST) return false;
		charge -= USE_COST;
		updateQuickslot();
		return true;
	}

	@Override public String status() { return Integer.toString(charge / 10); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }
}
