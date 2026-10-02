/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.damagetype.DamageType;
import render.utils.math.Random;

public class DewAmmo extends SpAmmo {
	@Override public void onHit(Char attacker, Char defender, int damage) {
		int bound = (int)(0.20f * Math.max(0, damage));
		defender.damage(elementRoll(bound), DamageType.ENERGY_DAMAGE);
		defender.damage(elementRoll(bound), DamageType.FIRE_DAMAGE);
		defender.damage(elementRoll(bound), DamageType.LIGHT_DAMAGE);
		defender.damage(elementRoll(bound), DamageType.DARK_DAMAGE);
		defender.damage(elementRoll(bound), DamageType.SHOCK_DAMAGE);
		defender.damage(elementRoll(bound), DamageType.ICE_DAMAGE);
		defender.damage(elementRoll(bound), DamageType.EARTH_DAMAGE);
	}
	private static int elementRoll(int bound) { return bound <= 1 ? 0 : Random.Int(bound); }
}
