/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnergyArmor;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Random;

public class Zshield extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "Z.s"; }
	@Override protected int missileType() { return MagicMissile.SHADOW; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(Random.Int(level, level * 3), com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.DARK_DAMAGE);
			Buff.affect(Dungeon.hero, EnergyArmor.class).level(level * 5);
		}
	}
	@Override public void onMeleeHit(com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1 && attacker instanceof com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero)
			Buff.affect(attacker, EnergyArmor.class).level(Math.max(1, ((com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero)attacker).lvl) * 3);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType.DARK_DAMAGE);
	}
}
