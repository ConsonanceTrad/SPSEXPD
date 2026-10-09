/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.items.misc.FourClover;
import pd.items.equipment.weapon.Weapon;
import render.utils.math.Random;

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
		defender.damage(applyBonus(attacker, (int)(legacyRoll(weapon, attacker) * scale), damageType), damageType);
		if (hasClover(attacker) && Random.Int(2) == 1) {
			defender.damage(applyBonus(attacker, (int)(legacyRoll(weapon, attacker) * 0.50f), damageType), damageType);
		}
	}

	//SPSEXPD: 英雄的元素伤害加成（统一属性层 HeroStats），非英雄攻击者原样返回
	private static int applyBonus(Char attacker, int dmg, Object damageType) {
		if (attacker instanceof pd.actors.hero.Hero && dmg > 0) {
			pd.actors.damagetype.Element e = pd.actors.damagetype.Element.of(damageType);
			if (e != null) {
				return pd.actors.hero.HeroStats.applyElement((pd.actors.hero.Hero) attacker, dmg, e);
			}
		}
		return dmg;
	}

	protected static boolean hasClover(Char attacker) {
		return attacker.buff(FourClover.FourCloverBless.class) != null;
	}
}
