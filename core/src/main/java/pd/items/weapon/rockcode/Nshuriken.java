/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.MagicMissile;
import pd.items.weapon.missiles.MegaCannon;
import pd.mechanics.Ballistica;
import watabou.utils.Random;

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
	@Override public void onMeleeHit(pd.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), MegaCannon.class);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), MegaCannon.class);
	}
}
