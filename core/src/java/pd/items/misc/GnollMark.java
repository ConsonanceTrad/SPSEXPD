/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

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
import pd.utils.GLog;
import pd.windows.WndUseItem;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentNonEquipDict;

public class GnollMark extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GnollMark.class)
			.t("name", "仪式面具")
			.t("ac_choose", "选择仪式")
			.t("ac_light", "光明仪式")
			.t("ac_dark", "黑暗仪式")
			.t("ac_earth", "自然仪式")
			.t("ac_life", "生命献祭")
			.t("break", "仪式面具尚未准备妥当。")
			.t("charge", "准备完成度：%1$d / %2$d。")
			.t("desc", "豺狼人萨满所佩戴的仪式面具，释放法杖可以提高准备度。光明仪式会大幅提升物理力量，但暂时无法使用法杖；黑暗仪式会使法杖伤害翻倍，但令使用者虚弱且无法攻击；自然仪式会提供护盾与再生。也可以献祭生命使面具完全准备就绪。");
	}



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
		image = EquipmentNonEquipDict.RITUAL_MASK;
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
