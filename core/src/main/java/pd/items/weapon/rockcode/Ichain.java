/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.actors.buffs.Paralysis;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import render.utils.Random;

public class Ichain extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "I.c"; }
	@Override protected int missileType() { return MagicMissile.WOOL; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(2 * Random.Int(level, level * 3), Dungeon.hero);
			if (target.isAlive() && Random.Int(4) == 0) Buff.affect(target, HolyStun.class, 3f);
		}
	}
	@Override public void onMeleeHit(pd.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) Buff.affect(defender, Paralysis.class, 3f);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), attacker);
	}
}
