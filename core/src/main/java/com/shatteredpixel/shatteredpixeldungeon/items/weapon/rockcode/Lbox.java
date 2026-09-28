/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostIce;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Random;

public class Lbox extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "L.b"; }
	@Override protected int missileType() { return MagicMissile.FROST; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(Random.Int(level, level * 3), com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.ICE_DAMAGE);
			if (target.isAlive()) Buff.affect(target, FrostIce.class).level(5);
		}
	}
	@Override public void onMeleeHit(com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) Buff.affect(defender, FrostIce.class).level(5);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.ICE_DAMAGE);
	}
}
