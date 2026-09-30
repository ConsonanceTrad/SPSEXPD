/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.rockcode;

import pd.actors.Char;
import pd.effects.MagicMissile;
import pd.items.bombs.DungeonBomb;
import pd.items.bombs.MiniBomb;
import pd.mechanics.Ballistica;
import render.utils.Random;

public class Bmech extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "B.m"; }
	@Override protected int missileType() { return MagicMissile.FIRE; }
	@Override protected void onZap(Ballistica bolt) { new DungeonBomb().explode(bolt.collisionPos); }
	@Override public void onMeleeHit(pd.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) new MiniBomb().explode(defender.pos);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), pd.actors.damagetype.DamageType.ENERGY_DAMAGE);
	}
}
