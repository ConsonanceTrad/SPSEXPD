/*
 * Special Surprise Pixel Dungeon
 * Copyright (C) 2014-2021 hmdzl001
 *
 * Distributed under the GNU General Public License v3 or later.
 */

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;

/** Shared runtime behavior for the armor-destroying SPS puzzle trap. */
public final class FleecingTrap {

	private FleecingTrap() {
	}

	public static boolean destroyArmor(Hero hero) {
		if (hero == null || hero.belongings == null) return false;
		Armor armor = hero.belongings.armor;
		return armor != null && armor.forceUnequipWithoutTime(hero);
	}
}
