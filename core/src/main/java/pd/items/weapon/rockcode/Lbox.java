/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FrostIce;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import watabou.utils.Random;

public class Lbox extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "L.b"; }
	@Override protected int missileType() { return MagicMissile.FROST; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(Random.Int(level, level * 3), pd.actors.damagetype.DamageType.ICE_DAMAGE);
			if (target.isAlive()) Buff.affect(target, FrostIce.class).level(5);
		}
	}
	@Override public void onMeleeHit(pd.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) Buff.affect(defender, FrostIce.class).level(5);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), pd.actors.damagetype.DamageType.ICE_DAMAGE);
	}
}
