/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.rockcode;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

public class Trush extends RockCode {
	{ collisionProperties = Ballistica.PROJECTILE; sname = "T.r"; }
	@Override protected int missileType() { return MagicMissile.EARTH; }
	@Override protected void onZap(Ballistica bolt) {
		int cell = bolt.collisionPos;
		Char target = Actor.findChar(cell);
		if (target != null) {
			int level = Math.max(1, Dungeon.hero.lvl);
			target.damage(4 * Random.Int(level, level * 3), Dungeon.hero);
		}
		int terrain = Dungeon.level.map[cell];
		if (terrain != Terrain.WELL && terrain != Terrain.EMPTY_WELL && terrain != Terrain.ENTRANCE
				&& terrain != Terrain.EXIT && terrain != Terrain.ALCHEMY && terrain != Terrain.IRON_MAKER) {
			Level.set(cell, Terrain.EMPTY_DECO, Dungeon.level);
			GameScene.updateMap(cell);
		}
	}
	@Override public void onMeleeHit(com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon weapon, Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 1) defender.damage(3 * Random.Int(Math.max(1, weapon.damageRoll(attacker))), attacker);
	}
}
