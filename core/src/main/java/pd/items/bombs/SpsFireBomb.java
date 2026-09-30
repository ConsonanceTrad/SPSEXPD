/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.bombs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.blobs.TarGas;
import pd.effects.CellEmitter;
import pd.effects.particles.SmokeParticle;
import pd.items.Item;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SpsFireBomb extends Bomb {
	{ image = ItemSpriteSheet.LEGACY_FIRE_BOMB; }
	@Override public void explode(int cell) {
		super.explode(cell);
		for (int offset : PathFinder.NEIGHBOURS9) {
			int target = cell + offset;
			if (!Dungeon.level.insideMap(target)) continue;
			if (Dungeon.level.heroFOV[target]) CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			GameScene.add(Blob.seed(target, 10, Fire.class));
			GameScene.add(Blob.seed(target, 10, TarGas.class));
			Char ch = Actor.findChar(target);
			if (ch != null) {
				int damage = Random.NormalIntRange(ch.HT / 12, Math.max(ch.HT / 12, ch.HT / 7)) - Math.max(ch.drRoll(), 0);
				if (damage > 0) ch.damage(damage, this);
			}
		}
	}
	@Override public boolean isIdentified() { return true; }
	@Override public Item random() { return this; }
	@Override public int value() { return 20 * quantity; }
}
