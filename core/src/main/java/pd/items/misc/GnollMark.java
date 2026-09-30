/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.buffs.Arcane;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Disarm;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.HighLight;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Rhythm;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Silent;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import pd.windows.WndUseItem;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class GnollMark extends Item {
	public static final String AC_LIGHT = "LIGHT";
	public static final String AC_DARK = "DARK";
	public static final String AC_EARTH = "EARTH";
	public static final String AC_LIFE = "LIFE";
	public static final String AC_CHOOSE = "CHOOSE";
	public static final int FULL_CHARGE = 60;
	public static final int RITE_COST = 30;
	private static final String CHARGE = "charge";

	private int charge;

	{
		image = ItemSpriteSheet.SPS_GNOLL_MARK;
		defaultAction = AC_CHOOSE;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= RITE_COST) {
			actions.add(AC_LIGHT);
			actions.add(AC_DARK);
			actions.add(AC_EARTH);
		}
		if (hero != null && hero.HP > hero.HT / 5) actions.add(AC_LIFE);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) {
			GameScene.show(new WndUseItem(null, this));
		} else if (AC_LIGHT.equals(action)) {
			if (!lightRite(hero)) GLog.i(Messages.get(this, "break"));
		} else if (AC_DARK.equals(action)) {
			if (!darkRite(hero)) GLog.i(Messages.get(this, "break"));
		} else if (AC_EARTH.equals(action)) {
			if (!earthRite(hero)) GLog.i(Messages.get(this, "break"));
		} else if (AC_LIFE.equals(action)) {
			if (!sacrifice(hero)) GLog.i(Messages.get(this, "break"));
		} else {
			super.execute(hero, action);
		}
	}

	public boolean lightRite(Hero hero) {
		if (!consumeRite(hero)) return false;
		Buff.affect(hero, HighLight.class, 40f);
		Buff.affect(hero, AttackUp.class, 40f).level(80);
		Buff.affect(hero, DefenceUp.class, 40f).level(80);
		Buff.affect(hero, Silent.class, 40f);
		Buff.affect(hero, Locked.class, 40f);
		finishRite(hero);
		return true;
	}

	public boolean darkRite(Hero hero) {
		if (!consumeRite(hero)) return false;
		Buff.affect(hero, Recharging.class, 40f);
		Buff.affect(hero, Arcane.class, 5f);
		Buff.affect(hero, STRDown.class, 40f);
		Buff.affect(hero, Disarm.class, 40f);
		finishRite(hero);
		return true;
	}

	public boolean earthRite(Hero hero) {
		if (!consumeRite(hero)) return false;
		Buff.affect(hero, EnergyArmor.class).level(hero.HT / 5);
		Buff.affect(hero, BerryRegeneration.class).level(hero.HT / 5);
		Buff.affect(hero, Rhythm.class, 40f);
		finishRite(hero);
		return true;
	}

	public boolean sacrifice(Hero hero) {
		if (hero == null || hero.HP <= hero.HT / 5) return false;
		hero.HP -= hero.HT / 5;
		charge = FULL_CHARGE;
		finishRite(hero);
		return true;
	}

	private boolean consumeRite(Hero hero) {
		if (hero == null || charge < RITE_COST) return false;
		charge -= RITE_COST;
		return true;
	}

	private void finishRite(Hero hero) {
		hero.spendAndNext(1f);
		updateQuickslot();
	}

	public void gainCharge() { if (charge < FULL_CHARGE) charge++; }
	public void gainCharge(int amount) { charge = Math.min(FULL_CHARGE, charge + Math.max(0, amount)); }
	public int charge() { return charge; }

	@Override public String status() { return Integer.toString(charge / RITE_COST); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }
}
