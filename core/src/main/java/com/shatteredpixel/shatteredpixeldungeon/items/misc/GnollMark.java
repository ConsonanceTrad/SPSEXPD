/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Arcane;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenceUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Disarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnergyArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HighLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Locked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Rhythm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.STRDown;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
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
