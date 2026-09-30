/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.bombs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SlowGas;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Shocked;
import pd.effects.CellEmitter;
import pd.effects.particles.SmokeParticle;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import render.noosa.audio.Sample;
import render.utils.data.BArray;
import render.utils.math.Random;

public class StormBomb extends Bomb {
	{ image = ItemSpriteSheet.LEGACY_STORM_BOMB; }
	@Override public void explode(int cell) {
		super.explode(cell);
		PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) GameScene.add(Blob.seed(i, 20, ElectriShock.class));
		}
		for (int offset : PathFinder.NEIGHBOURS9) {
			int target = cell + offset;
			if (!Dungeon.level.insideMap(target)) continue;
			if (Dungeon.level.heroFOV[target]) CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			GameScene.add(Blob.seed(target, 10, SlowGas.class));
			Char ch = Actor.findChar(target);
			if (ch != null) {
				Buff.affect(ch, Shocked.class).level(10);
				int damage = Random.NormalIntRange(ch.HT / 12, Math.max(ch.HT / 12, ch.HT / 6)) - Math.max(ch.drRoll(), 0);
				if (damage > 0) ch.damage(damage, this);
			}
		}
		Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
	}
	@Override public StormBomb random() { return this; }
	@Override public int value() { return 20 * quantity; }
}
