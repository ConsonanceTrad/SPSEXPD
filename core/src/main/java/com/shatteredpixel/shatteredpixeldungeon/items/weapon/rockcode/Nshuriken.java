/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MegaCannon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Random;

public class Nshuriken extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "N.s"; }
	@Override protected int missileType() { return MagicMissile.LIGHT_MISSILE; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(3 * Random.Int(level, level * 3), MegaCannon.class);
		}
	}
	@Override public void onMeleeHit(com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), MegaCannon.class);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), MegaCannon.class);
	}
}
