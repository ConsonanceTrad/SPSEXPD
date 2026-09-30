/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.effects.BlobEmitter;
import pd.effects.particles.ShaftParticle;
import pd.levels.Level;
import watabou.utils.Bundle;

/** Legacy single-cell portal visual. Teleport rules remain owned by the fixed levels. */
public class Portal extends Blob {
	private int pos = -1;

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (cur != null) for (int cell = 0; cell < cur.length; cell++) {
			if (cur[cell] > 0) { pos = cell; break; }
		}
	}

	@Override protected void evolve() {
		if (pos >= 0 && pos < cur.length) volume = off[pos] = cur[pos];
	}

	@Override public void seed(Level level, int cell, int amount) {
		if (cur == null) cur = new int[level.length()];
		if (off == null) off = new int[cur.length];
		if (pos >= 0 && pos < cur.length) cur[pos] = 0;
		pos = cell;
		volume = cur[pos] = amount;
		area.setEmpty();
		area.union(cell % level.width(), cell / level.width());
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(ShaftParticle.FACTORY, 0.9f, 0);
	}
}
