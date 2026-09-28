/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Random;

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
	@Override public void onMeleeHit(com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) dealElements(defender, Math.max(1, weapon.damageRoll(attacker) / 6));
	}
	private static void dealElements(Char target, int damage) {
		target.damage(damage, DamageType.ENERGY_DAMAGE); target.damage(damage, DamageType.EARTH_DAMAGE);
		target.damage(damage, DamageType.FIRE_DAMAGE); target.damage(damage, DamageType.ICE_DAMAGE);
		target.damage(damage, DamageType.SHOCK_DAMAGE); target.damage(damage, DamageType.LIGHT_DAMAGE);
		target.damage(damage, DamageType.DARK_DAMAGE);
	}
}
