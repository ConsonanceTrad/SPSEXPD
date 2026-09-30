/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.buffs.Arcane;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.GlassShield;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Rhythm2;
import pd.actors.buffs.Rhythm;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import pd.windows.WndUseItem;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class DanceLion extends Item {

	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_SPIN = "SPIN";
	public static final String AC_STAND = "STAND";
	public static final String AC_BACK = "BACK";
	public static final String AC_RUSH = "RUSH";
	public static final String AC_JUMP = "JUMP";
	public static final int FULL_CHARGE = 100;
	public static final int USE_COST = 40;
	private static final String CHARGE = "charge";

	private int charge;

	{ image = ItemSpriteSheet.SPS_DANCE_LION; unique = true; defaultAction = AC_CHOOSE; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		if (charge >= USE_COST) {
			actions.add(AC_SPIN);
			actions.add(AC_STAND);
			actions.add(AC_BACK);
			actions.add(AC_RUSH);
			actions.add(AC_JUMP);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) {
			GameScene.show(new WndUseItem(null, this));
			return;
		}
		if (!isDanceAction(action)) {
			super.execute(hero, action);
			return;
		}
		if (charge < USE_COST) {
			GLog.p(Messages.get(this, "need_charge"));
			return;
		}

		if (AC_SPIN.equals(action)) {
			Buff.prolong(hero, Rhythm.class, 20f);
			if (hero.subClass == HeroSubClass.SUPERSTAR) Buff.prolong(hero, Rhythm2.class, 20f);
		} else if (AC_STAND.equals(action)) {
			Buff.prolong(hero, DefenceUp.class, 20f).level(30);
			if (hero.subClass == HeroSubClass.SUPERSTAR) Buff.affect(hero, EnergyArmor.class).level(hero.lvl * 2);
		} else if (AC_BACK.equals(action)) {
			Buff.prolong(hero, Recharging.class, 10f);
			if (hero.subClass == HeroSubClass.SUPERSTAR) Buff.prolong(hero, Arcane.class, 4f);
		} else if (AC_RUSH.equals(action)) {
			Buff.prolong(hero, AttackUp.class, 20f).level(30);
			if (hero.subClass == HeroSubClass.SUPERSTAR) Buff.prolong(hero, Invisibility.class, 20f);
		} else {
			Buff.prolong(hero, Levitation.class, 20f);
			if (hero.subClass == HeroSubClass.SUPERSTAR) Buff.affect(hero, GlassShield.class).turns(2);
		}
		charge -= USE_COST;
		updateQuickslot();
		hero.spendAndNext(1f);
	}

	private static boolean isDanceAction(String action) {
		return AC_SPIN.equals(action) || AC_STAND.equals(action) || AC_BACK.equals(action)
				|| AC_RUSH.equals(action) || AC_JUMP.equals(action);
	}

	public void gainCharge() {
		if (charge < FULL_CHARGE) {
			charge++;
			updateQuickslot();
		}
	}

	public int charge() { return charge; }
	public void charge(int value) { charge = Math.max(0, Math.min(FULL_CHARGE, value)); updateQuickslot(); }
	@Override public String status() { return Integer.toString(charge / USE_COST); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge(bundle.getInt(CHARGE)); }
}
