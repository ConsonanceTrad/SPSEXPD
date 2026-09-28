/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DiceTower extends Item {

	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_ROLL = "ROLL";
	public static final String AC_REROLL = "REROLL";
	public static final String AC_ALLIN = "ALLIN";
	public static final int FULL_CHARGE = 100;
	public static final int CHEAT_COST = 60;
	private static final String CHARGE = "charge";
	private int charge;

	{ image = ItemSpriteSheet.SPS_DICE_TOWER; unique = true; defaultAction = AC_CHOOSE; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_ROLL);
		actions.add(AC_REROLL);
		actions.add(AC_ALLIN);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) GameScene.show(new WndUseItem(null, this));
		else if (AC_ROLL.equals(action)) roll(hero);
		else if (AC_REROLL.equals(action)) { if (!cheat(hero)) GLog.p(Messages.get(this, "need_charge")); }
		else if (AC_ALLIN.equals(action)) allIn(hero);
		else super.execute(hero, action);
	}

	public boolean roll(Hero hero) {
		if (hero == null) return false;
		hero.spp = Random.Int(100);
		hero.spendAndNext(1f);
		return true;
	}

	public boolean cheat(Hero hero) {
		if (hero == null || charge < CHEAT_COST) return false;
		hero.spp = 100;
		charge -= CHEAT_COST;
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public boolean allIn(Hero hero) {
		if (hero == null) return false;
		hero.spp += Dungeon.gold / 5000;
		Dungeon.gold = 0;
		hero.spendAndNext(1f);
		return true;
	}

	public void gainCharge() { if (charge < FULL_CHARGE) { charge++; updateQuickslot(); } }
	public int charge() { return charge; }
	public void charge(int value) { charge = Math.max(0, Math.min(FULL_CHARGE, value)); updateQuickslot(); }
	@Override public String status() { return Integer.toString(charge / CHEAT_COST); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge(bundle.getInt(CHARGE)); }
}
