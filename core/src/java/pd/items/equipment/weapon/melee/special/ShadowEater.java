/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Silent;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.items.Item;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

/** The original tester-forged cursed blade and its kill-charge awakening. */
public class ShadowEater extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShadowEater.class)
			.t("name", "暗噬")
			.t("desc", "由测试者们合作制成的受诅咒武器，净化会压制它的力量。\n休眠、双刃、低语。")
			.t("charge", "暗影：%1$d / %2$d")
			.t("ac_awake", "唤醒")
			.t("ac_uncurse", "驱逐")
			.t("awake", "血……")
			.t("bleeding", "无需血肉……")
			.t("silent", "无需言语……")
			.t("locked", "无需帮助……")
			.t("stand", "无需移动……");
	}



	public static final String AC_AWAKE = "AWAKE";
	public static final String AC_UNCURSE = "UNCURSE";
	public static final int MAX_CHARGE = 20;

	private int charge;

	{
		image = EquipmentEquipWeaponBasicWeaponDict.BLOOD_BLADE;
		tier = 4;
		ACC = 1f;
		DLY = 1f;
		RCH = 1;
		reinforced = true;
		cursed = true;
	}

	@Override public int min(int lvl) { return 9 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 24 + 3 * Math.max(0, lvl); }
	@Override public int STRReq(int lvl) { return 15; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= MAX_CHARGE) actions.add(AC_AWAKE);
		actions.add(AC_UNCURSE);
		return actions;
	}

	@Override
	public Item uncurse() {
		return this;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_AWAKE.equals(action) && charge >= MAX_CHARGE) {
			cursed = true;
			charge = 0;
			GLog.i(Messages.get(this, "awake"));
			Buff.affect(hero, AttackUp.class, 30f).level(300);
			Buff.affect(hero, Bleeding.class).set(hero.HT / 2f);
			updateQuickslot();
		} else if (AC_UNCURSE.equals(action)) {
			cursed = false;
			updateQuickslot();
		} else {
			super.execute(hero, action);
		}
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) > 70) {
			damage = Math.round(damage * 1.5f);
			if (attacker.sprite != null) {
				attacker.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.3f, 3);
			}
			if (Random.Int(4) == 0) {
				if (attacker.sprite != null) attacker.sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "bleeding"));
				Buff.affect(attacker, Bleeding.class).set(20);
			} else if (Random.Int(3) == 0) {
				if (attacker.sprite != null) attacker.sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "locked"));
				Buff.affect(attacker, Locked.class, 5f);
			} else if (Random.Int(2) == 0) {
				if (attacker.sprite != null) attacker.sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "stand"));
				Buff.affect(attacker, Cripple.class, 5f);
			} else {
				if (attacker.sprite != null) attacker.sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "silent"));
				Buff.affect(attacker, Silent.class, 5f);
			}
		}
		if (defender.HP <= damage && charge < MAX_CHARGE) {
			charge++;
			updateQuickslot();
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String desc() {
		return super.desc() + "\n\n" + Messages.get(this, "charge", charge, MAX_CHARGE);
	}

	public int charge() { return charge; }
	@Override public String statsInfo() { return ""; }
	@Override public String abilityInfo() { return ""; }

	private static final String CHARGE = "charge";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(MAX_CHARGE, bundle.getInt(CHARGE)));
	}
}
