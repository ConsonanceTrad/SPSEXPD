/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.start;

import pd.actors.Char;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

public class BeastKnive extends NormalMeleeWeapon {

	public static final String AC_ZAP = "ZAP";
	public static final int ZAP_COST = 10;
	private static final String CHARGE = "charge";
	private int charge;

	public BeastKnive() {
		super(1, 1f, 0.5f, 1, 4, 10, ItemSpriteSheet.SPS_WEP_DAGGER);
		unique = true;
		reinforced = true;
		cursed = true;
		defaultAction = AC_ZAP;
	}

	@Override protected void applyLegacyUpgrade(Stats stats) { stats.max++; }
	@Override public Item uncurse() { return this; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= ZAP_COST) actions.add(AC_ZAP);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_ZAP.equals(action)) {
			if (!empower(hero)) GLog.w(Messages.get(this, "no"));
		} else super.execute(hero, action);
	}

	public boolean empower(Hero hero) {
		if (hero == null || charge < ZAP_COST) return false;
		Buff.affect(hero, AttackUp.class, 10f).level(30);
		charge -= ZAP_COST;
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int low = Math.max(0, damage / 4);
		int high = Math.max(low, damage / 2);
		if (high > 0) defender.damage(high <= low ? low : Random.Int(low, high), this);
		charge++;
		if (Random.Int(100) < 40) Buff.affect(defender, Bleeding.class).set(Math.max(1, high <= 5 ? 5 : Random.Int(5, high)));
		updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	public int charge() { return charge; }
	public void charge(int value) { charge = Math.max(0, value); updateQuickslot(); }
	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "charge", charge, ZAP_COST); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge(bundle.getInt(CHARGE)); }
}
