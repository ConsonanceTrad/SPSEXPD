/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShadowCurse;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Random;

public class Dpotion extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "D.p"; }
	@Override protected int missileType() { return MagicMissile.SHADOW; }

	@Override protected void onZap(Ballistica bolt) {
		int level = Math.max(1, Dungeon.hero.lvl);
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			target.damage(Random.Int(level, level * 3), this);
			if (target.isAlive() && Random.Int(2) == 0) Buff.affect(target, ShadowCurse.class);
		}
	}

	@Override public void onMeleeHit(com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon weapon,
			Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) Buff.affect(defender, ShadowCurse.class);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))),
				com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.DARK_DAMAGE);
	}
}
