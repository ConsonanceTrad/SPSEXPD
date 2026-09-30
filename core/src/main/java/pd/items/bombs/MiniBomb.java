/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.bombs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

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
