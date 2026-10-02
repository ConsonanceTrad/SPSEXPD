/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.start;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class BeastKnive extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BeastKnive.class)
			.t("name", "兽性匕首")
			.t("ac_zap", "狂怒")
			.t("no", "匕首需要10点充能。")
			.t("charge", "充能：%1$d / %2$d。")
			.t("desc", "浸润兽血的小刀。命中会造成额外伤害、可能施加流血并积蓄充能；“狂怒”消耗10点充能，暂时提高30%%攻击伤害。");
	}




	public static final String AC_ZAP = "ZAP";
	public static final int ZAP_COST = 10;
	private static final String CHARGE = "charge";
	private int charge;

	public BeastKnive() {
		super(1, 1f, 0.5f, 1, 4, 10, SpecificPlaceHolderDict.SOMETHING_0);
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
