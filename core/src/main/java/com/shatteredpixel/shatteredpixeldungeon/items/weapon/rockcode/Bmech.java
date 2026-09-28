/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.DungeonBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.MiniBomb;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Random;

public class Bmech extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "B.m"; }
	@Override protected int missileType() { return MagicMissile.FIRE; }
	@Override protected void onZap(Ballistica bolt) { new DungeonBomb().explode(bolt.collisionPos); }
	@Override public void onMeleeHit(com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) new MiniBomb().explode(defender.pos);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.ENERGY_DAMAGE);
	}
}
