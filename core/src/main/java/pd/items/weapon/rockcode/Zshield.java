/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EnergyArmor;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import watabou.utils.Random;

public class Zshield extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "Z.s"; }
	@Override protected int missileType() { return MagicMissile.SHADOW; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(Random.Int(level, level * 3), pd.actors.damagetype.DamageType.DARK_DAMAGE);
			Buff.affect(Dungeon.hero, EnergyArmor.class).level(level * 5);
		}
	}
	@Override public void onMeleeHit(pd.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1 && attacker instanceof pd.actors.hero.Hero)
			Buff.affect(attacker, EnergyArmor.class).level(Math.max(1, ((pd.actors.hero.Hero)attacker).lvl) * 3);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), pd.actors.damagetype.DamageType.DARK_DAMAGE);
	}
}
