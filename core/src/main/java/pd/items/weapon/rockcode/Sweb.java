/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.rockcode;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Web;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Roots;
import pd.effects.MagicMissile;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import watabou.utils.PathFinder;
import watabou.utils.Random;

public class Sweb extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "S.w"; }
	@Override protected int missileType() { return MagicMissile.LIGHT_MISSILE; }
	@Override protected void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(Random.Int(level, level * 3), Dungeon.hero);
		}
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = bolt.collisionPos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]) GameScene.add(Blob.seed(cell, 4, Web.class));
		}
	}
	@Override public void onMeleeHit(pd.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) Buff.affect(defender, Roots.class, 3f);
		defender.damage(Random.Int(Math.max(1, weapon.damageRoll(attacker))), attacker);
	}
}
