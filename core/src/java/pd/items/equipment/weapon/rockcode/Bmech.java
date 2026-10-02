/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.rockcode;

import pd.actors.Char;
import pd.effects.MagicMissile;
import pd.items.equipment.bombs.DungeonBomb;
import pd.items.equipment.bombs.MiniBomb;
import pd.mechanics.Ballistica;
import render.utils.math.Random;

public class Bmech extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "B.m"; }
	@Override protected int missileType() { return MagicMissile.FIRE; }
	@Override protected void onZap(Ballistica bolt) { new DungeonBomb().explode(bolt.collisionPos); }
	@Override public void onMeleeHit(pd.items.equipment.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) new MiniBomb().explode(defender.pos);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), pd.actors.damagetype.DamageType.ENERGY_DAMAGE);
	}
}
