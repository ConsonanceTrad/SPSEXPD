/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.damagetype.DamageType;
import pd.actors.damagetype.Element;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroStats;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/** A 99-charge tester wand with nine selectable elemental damage types. */
public class WandOfTest extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfTest.class)
			.t("name", "测试法杖")
			.t("ac_0", "能量")
			.t("ac_1", "火焰")
			.t("ac_2", "寒冰")
			.t("ac_3", "雷电")
			.t("ac_4", "大地")
			.t("ac_5", "光明")
			.t("ac_6", "黑暗")
			.t("ac_7", "风")
			.t("ac_8", "混乱")
			.t("desc", "这根法杖是测试用的，可以改变造成伤害的属性。")
			.t("stats_desc", "这根法杖射出的飞弹能造成_%1$d~%2$d点伤害_。");
	}




	public static final String AC_ENERGY = "0";
	public static final String AC_FIRE = "1";
	public static final String AC_ICE = "2";
	public static final String AC_SHOCK = "3";
	public static final String AC_EARTH = "4";
	public static final String AC_LIGHT = "5";
	public static final String AC_DARK = "6";
	//SPSEXPD: 新增风、混乱两系，与 Element 九系对齐
	public static final String AC_WIND = "7";
	public static final String AC_CHAOS = "8";
	private static final String TYPE = "type";

	private int type;

	{
		image = SpecificPlaceHolderDict.SPS_PH_WAND_TEST;
		collisionProperties = Ballistica.MAGIC_BOLT;
	}

	@Override public int min(int lvl) { return 10 + lvl; }
	@Override public int max(int lvl) { return 10 + 2 * lvl; }
	@Override public int initialCharges() { return 99; }
	@Override public void updateLevel() { maxCharges = 99; curCharges = Math.min(curCharges, maxCharges); }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ENERGY);
		actions.add(AC_FIRE);
		actions.add(AC_ICE);
		actions.add(AC_SHOCK);
		actions.add(AC_EARTH);
		actions.add(AC_LIGHT);
		actions.add(AC_DARK);
		actions.add(AC_WIND);
		actions.add(AC_CHAOS);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (isTypeAction(action)) {
			type(Integer.parseInt(action));
			return;
		}
		super.execute(hero, action);
	}

	private static boolean isTypeAction(String action) {
		return AC_ENERGY.equals(action) || AC_FIRE.equals(action) || AC_ICE.equals(action)
				|| AC_SHOCK.equals(action) || AC_EARTH.equals(action) || AC_LIGHT.equals(action)
				|| AC_DARK.equals(action) || AC_WIND.equals(action) || AC_CHAOS.equals(action);
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target == null) return;
		wandProc(target, chargesPerCast());
		//SPSEXPD: 输出端元素增幅（统一属性层 HeroStats，与 SpsEnchantment 同源）——
		//按当前档位的元素放大伤害，使元素加成在实测中真正生效
		int dmg = damageRoll();
		if (curUser instanceof Hero) {
			Element element = Element.of(damageType());
			if (element != null) dmg = HeroStats.applyElement((Hero) curUser, dmg, element);
		}
		target.damage(dmg, damageType());
		if (target.sprite != null) target.sprite.burst(0xFF99CCFF, 2);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD's test wand has no battlemage on-hit effect.
	}

	public DamageType damageType() {
		switch (type) {
			case 1: return DamageType.FIRE_DAMAGE;
			case 2: return DamageType.ICE_DAMAGE;
			case 3: return DamageType.SHOCK_DAMAGE;
			case 4: return DamageType.EARTH_DAMAGE;
			case 5: return DamageType.LIGHT_DAMAGE;
			case 6: return DamageType.DARK_DAMAGE;
			case 7: return DamageType.WIND_DAMAGE;
			case 8: return DamageType.CHAOS_DAMAGE;
			default: return DamageType.ENERGY_DAMAGE;
		}
	}

	@Override
	public void wandUsed() {
		int before = curCharges;
		super.wandUsed();
		if (before > 0 && curCharges <= 0 && curUser != null) {
			curUser.spp += 100;
			detach(curUser.belongings.backpack);
		}
	}

	public int type() { return type; }
	public void type(int value) { type = Math.max(0, Math.min(8, value)); }

	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "ac_" + type); }
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TYPE, type);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		type(bundle.getInt(TYPE));
	}
}
