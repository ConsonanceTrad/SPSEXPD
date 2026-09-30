/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.enchantments;

import pd.actors.Char;
import pd.items.misc.FourClover;
import pd.items.weapon.Weapon;
import render.utils.Random;

/** Shared SPS-PD 0.9.8 elemental damage rolls with repaired low-level bounds. */
abstract class SpsEnchantment extends Weapon.Enchantment {

	static int legacyLevel(Char attacker) {
		return Math.max(0, Math.min(20, attacker.HT / 10));
	}

	static int legacyRoll(Weapon weapon, Char attacker) {
		int minimum = legacyLevel(attacker);
		int maximum = minimum + weapon.level();
		return maximum <= minimum ? minimum : Random.Int(minimum, maximum);
	}

	protected final void elementalDamage(Weapon weapon, Char attacker, Char defender,
			float scale, Object damageType) {
		defender.damage((int)(legacyRoll(weapon, attacker) * scale), damageType);
		if (hasClover(attacker) && Random.Int(2) == 1) {
			defender.damage((int)(legacyRoll(weapon, attacker) * 0.50f), damageType);
		}
	}

	protected static boolean hasClover(Char attacker) {
		return attacker.buff(FourClover.FourCloverBless.class) != null;
	}
}
