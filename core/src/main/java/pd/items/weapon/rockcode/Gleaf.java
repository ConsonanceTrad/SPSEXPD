/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.effects.MagicMissile;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import render.utils.math.Random;

public class Gleaf extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "G.l"; }
	@Override protected int missileType() { return MagicMissile.FOLIAGE; }

	@Override protected void onZap(Ballistica bolt) {
		int level = Math.max(1, Dungeon.hero.lvl);
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			target.damage(Random.Int(level, level * 3), this);
			if (target.isAlive() && Random.Int(2) == 0) {
				Buff.affect(target, Poison.class).set(level);
				Buff.affect(target, Roots.class, 3f);
			}
		}
		int terrain = Dungeon.level.map[bolt.collisionPos];
		if (terrain != Terrain.WELL && terrain != Terrain.EMPTY_WELL
				&& terrain != Terrain.ENTRANCE && terrain != Terrain.EXIT
				&& terrain != Terrain.ALCHEMY && terrain != Terrain.IRON_MAKER) {
			Level.set(bolt.collisionPos, Terrain.OLD_HIGH_GRASS);
			GameScene.updateMap(bolt.collisionPos);
		}
	}

	@Override public void onMeleeHit(pd.items.weapon.melee.MeleeWeapon weapon,
			Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) {
			Buff.affect(defender, Poison.class).set(Math.max(0, damage / 2));
			Buff.affect(defender, Roots.class, 3f);
		}
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))),
				pd.actors.damagetype.DamageType.EARTH_DAMAGE);
	}
}
