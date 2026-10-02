/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.actors.hero.Hero;
import pd.items.weapon.Weapon;
import pd.items.weapon.melee.MeleeWeapon;
import pd.items.weapon.ranges.RangePan;
import render.utils.math.Random;

import java.util.ArrayList;

/** Coconut2's pan in its fixed-damage melee form. */
public class MeleePan extends MeleeWeapon {

	public static final String AC_CHANGE = "CHANGE";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 1;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return 8 + Math.max(0, lvl); }
	@Override public int max(int lvl) { return 8 + Math.max(0, lvl); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30) Buff.prolong(defender, HolyStun.class, 2f);
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
		if (AC_CHANGE.equals(action) && isEquipped(hero)) changeToRange(hero);
		else super.execute(hero, action);
	}

	public RangePan changeToRange(Hero hero) {
		RangePan replacement = new RangePan();
		copyState(this, replacement);
		hero.belongings.weapon = replacement;
		return replacement;
	}

	public static void copyState(Weapon source, Weapon replacement) {
		int level = source.trueLevel();
		if (level > 0) replacement.upgrade(level);
		else if (level < 0) replacement.degrade(-level);
		replacement.enchantment = source.enchantment;
		replacement.reinforced = source.reinforced;
		replacement.levelKnown = source.levelKnown;
		replacement.cursedKnown = source.cursedKnown;
		replacement.cursed = source.cursed;
	}

	@Override public int value() { return legacyValue(this); }

	public static int legacyValue(Weapon weapon) {
		int result = weapon.enchantment == null ? 50 : 75;
		if (weapon.cursed && weapon.cursedKnown) result /= 2;
		if (weapon.levelKnown) {
			if (weapon.trueLevel() > 0) result *= weapon.trueLevel() + 1;
			else if (weapon.trueLevel() < 0) result /= 1 - weapon.trueLevel();
		}
		return Math.max(1, result);
	}
}
