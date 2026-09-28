/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class MiniBomb extends Bomb {
	{ image = ItemSpriteSheet.SPS_MINI_BOMB; }

	@Override public void explode(int cell) {
		super.explode(cell);
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;
		boolean terrainAffected = false;
		if (Dungeon.level.flamable[cell]) {
			Level.set(cell, Terrain.EMBERS, Dungeon.level);
			GameScene.updateMap(cell);
			terrainAffected = true;
		}
		Char target = Actor.findChar(cell);
		if (target != null && target.isAlive()) {
			int min = Math.max(0, target.HT / 15);
			int max = Math.max(min, target.HT / 6);
			int damage = Random.NormalIntRange(min, max) - Math.max(0, target.drRoll());
			if (damage > 0) target.damage(damage, this);
		}
		if (terrainAffected) Dungeon.observe();
	}

	@Override public int value() { return 10 * quantity; }
}
