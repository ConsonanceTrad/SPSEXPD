/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.mindbuff.AmokMind;
import pd.actors.buffs.mindbuff.CrazyMind;
import pd.actors.buffs.mindbuff.LoseMind;
import pd.actors.buffs.mindbuff.MindBuff;
import pd.actors.buffs.mindbuff.TerrorMind;
import pd.actors.buffs.mindbuff.WeakMind;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.weapon.missiles.throwing.MindArrow;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class PPC extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PPC.class)
			.t("name", "电子放大镜")
			.t("ac_try", "试探")
			.t("ac_heal", "疗养")
			.t("ac_mind", "追忆")
			.t("need_charge", "记录数不足。")
			.t("charge", "记录数：%d")
			.t("desc", "每当有生物死亡，它都会留下一条记录。消耗记录可以获得灵感、摆脱一种精神异常，或制造意识之矢。");
	}




	public static final String AC_TRY = "TRY";
	public static final String AC_HEAL = "HEAL";
	public static final String AC_MIND = "MIND";
	public static final int HEAL_COST = 20;
	public static final int MIND_COST = 2;
	private static final String CHARGE = "charge";
	private int charge;

	{ image = SpecificPlaceHolderDict.SOMETHING_0; defaultAction = AC_TRY; unique = true; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		actions.add(AC_TRY);
		if (charge >= HEAL_COST) actions.add(AC_HEAL);
		if (charge >= MIND_COST) actions.add(AC_MIND);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_TRY.equals(action)) { if (!input(hero)) GLog.p(Messages.get(this, "need_charge")); }
		else if (AC_HEAL.equals(action)) { if (!recover(hero)) GLog.p(Messages.get(this, "need_charge")); }
		else if (AC_MIND.equals(action)) { if (!remember(hero)) GLog.p(Messages.get(this, "need_charge")); }
		else super.execute(hero, action);
	}

	public boolean input(Hero hero) {
		if (hero == null || charge < 1) return false;
		charge--;
		hero.spp += Random.Int(10);
		Buff.prolong(hero, Bless.class, 10f);
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public boolean recover(Hero hero) {
		if (hero == null || charge < HEAL_COST) return false;
		charge -= HEAL_COST;
		for (Class<? extends MindBuff> type : mentalStates()) {
			MindBuff buff = hero.buff(type);
			if (buff != null) { buff.detach(); break; }
		}
		hero.HP = Math.min(hero.HT, hero.HP + hero.HT / 5);
		hero.spp = 0;
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public boolean remember(Hero hero) {
		if (hero == null || Dungeon.level == null || charge < MIND_COST) return false;
		charge -= MIND_COST;
		Heap heap = Dungeon.level.drop(new MindArrow(5), hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends MindBuff>[] mentalStates() {
		return new Class[]{CrazyMind.class, WeakMind.class, AmokMind.class, TerrorMind.class, LoseMind.class};
	}

	public void gainCharge() { charge++; updateQuickslot(); }
	public int charge() { return charge; }
	public void charge(int value) { charge = Math.max(0, value); updateQuickslot(); }
	@Override public String status() { return Integer.toString(charge); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge(bundle.getInt(CHARGE)); }
}
