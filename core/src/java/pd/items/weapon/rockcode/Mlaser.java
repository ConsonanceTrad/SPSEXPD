/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.damagetype.DamageType;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import render.utils.math.Random;

public class Mlaser extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "M.l"; }
	@Override protected int missileType() { return MagicMissile.RAINBOW; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target == null) return;
		int level = Math.max(1, Dungeon.hero.lvl);
		int part = Math.max(1, Random.Int(level, level * 3) / 6);
		dealElements(target, part);
	}
	@Override public void onMeleeHit(pd.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) dealElements(defender, Math.max(1, weapon.damageRoll(attacker) / 6));
	}
	private static void dealElements(Char target, int damage) {
		target.damage(damage, DamageType.ENERGY_DAMAGE); target.damage(damage, DamageType.EARTH_DAMAGE);
		target.damage(damage, DamageType.FIRE_DAMAGE); target.damage(damage, DamageType.ICE_DAMAGE);
		target.damage(damage, DamageType.SHOCK_DAMAGE); target.damage(damage, DamageType.LIGHT_DAMAGE);
		target.damage(damage, DamageType.DARK_DAMAGE);
	}
}
