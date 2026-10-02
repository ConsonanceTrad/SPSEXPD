/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.ranges;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.hero.Hero;
import pd.items.equipment.weapon.melee.special.MeleePan;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The pan's ranged form; it retains the original tier-one bow statistics. */
public class RangePan extends RangeWeapon {
	{
		image = SpecificPlaceHolderDict.SPS_PH_WEAPON_BAD;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RangePan.class)
			.t("name", "平底煎锅")
			.t("ac_change", "切换")
			.t("desc", "用于烹饪的煎锅。\n可以把食物打击出去");
	}




	public static final String AC_CHANGE = "CHANGE";

	public RangePan() {
		super(1, Variant.NORMAL, SpecificPlaceHolderDict.SOMETHING_0);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30) Buff.affect(defender, Burning.class).reignite(defender, 5f);
		return super.proc(attacker, defender, damage);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero)) actions.add(AC_CHANGE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHANGE.equals(action) && isEquipped(hero)) changeToMelee(hero);
		else super.execute(hero, action);
	}

	public MeleePan changeToMelee(Hero hero) {
		MeleePan replacement = new MeleePan();
		MeleePan.copyState(this, replacement);
		hero.belongings.weapon = replacement;
		return replacement;
	}

	@Override public int value() { return MeleePan.legacyValue(this); }
}
