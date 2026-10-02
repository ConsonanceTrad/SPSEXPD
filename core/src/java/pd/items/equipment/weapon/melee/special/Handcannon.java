/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Waterskin;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The five-shot, dew-fuelled hand cannon dropped by the void goo. */
public class Handcannon extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Handcannon.class)
			.t("name", "奥能火炮")
			.t("ac_onoff", "启动/关闭")
			.t("fuel", "低能源警告！")
			.t("off", "过载关闭！")
			.t("power_on", "过载模式启动！")
			.t("power_off", "过载模式关闭。")
			.t("desc", "人类发明了火器，矮人强化并改进了它。作为这个世界为数不多的火器之一，这件巨炮可以从很远的地方打击目标。——Consideredhamster \n???");
	}

	public static final String AC_ONOFF = "ONOFF";
	private boolean turnedOn;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_ONOFF;
		tier = 4;
		ACC = 0.7f;
		DLY = 2f;
		RCH = 7;
		reinforced = true;
	}

	@Override public int min(int lvl) { return 9 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 31 + 3 * Math.max(0, lvl); }

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ONOFF);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (!AC_ONOFF.equals(action)) { super.execute(hero, action); return; }
		turnedOn = !turnedOn;
		GLog.i(Messages.get(this, turnedOn ? "power_on" : "power_off"));
		updateQuickslot();
		hero.next();
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		damage = super.proc(attacker, defender, damage);
		Waterskin skin = Dungeon.hero == null ? null : Dungeon.hero.belongings.getItem(Waterskin.class);
		if (!turnedOn || skin == null) return damage;
		if (skin.checkVol() <= 10) {
			if (skin.checkVol() == 0) { turnedOn = false; GLog.n(Messages.get(this, "fuel")); }
			return damage;
		}
		for (int shot = 1; shot <= 5; shot++) {
			skin.sip();
			defender.damage(Math.max(1, (attacker.damageRoll() - shot) * 2), this);
			GLog.h("Vrrrrrr!");
		}
		return damage;
	}

	public boolean turnedOn() { return turnedOn; }

	@Override public String desc() {
		String info = super.desc();
		Waterskin skin = Dungeon.hero == null ? null : Dungeon.hero.belongings.getItem(Waterskin.class);
		if (skin != null) info += "\n\n" + skin.checkVol();
		if (turnedOn) info += "\n\nON";
		return info;
	}

	private static final String TURNED_ON = "turned_on";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(TURNED_ON, turnedOn); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); turnedOn = bundle.getBoolean(TURNED_ON); }
}
