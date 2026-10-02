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

public class BigBattery extends Item {

	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_USE = "USE";
	public static final String AC_ADD = "ADD";
	public static final int FULL_CHARGE = 20;
	public static final int USE_COST = 15;
	private static final String CHARGE = "charge";

	private int charge;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
