/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.bombs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SlowGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FrostIce;
import pd.effects.CellEmitter;
import pd.effects.particles.SmokeParticle;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.PathFinder;
import watabou.utils.Random;

public class IceBomb extends Bomb {
	{ image = ItemSpriteSheet.LEGACY_ICE_BOMB; }
	@Override public void explode(int cell) {
		super.explode(cell);
		for (int offset : PathFinder.NEIGHBOURS9) {
			int target = cell + offset;
			if (!Dungeon.level.insideMap(target)) continue;
			if (Dungeon.level.heroFOV[target]) CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			GameScene.add(Blob.seed(target, 10, SlowGas.class));
			Char ch = Actor.findChar(target);
			if (ch != null) {
				Buff.affect(ch, FrostIce.class).level(10);
				int damage = Random.NormalIntRange(ch.HT / 20, Math.max(ch.HT / 20, ch.HT / 10)) - Math.max(ch.drRoll(), 0);
				if (damage > 0) ch.damage(damage, this);
			}
		}
	}
	@Override public IceBomb random() { return this; }
	@Override public int value() { return 20 * quantity; }
}
