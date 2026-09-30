/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Dungeon;
import pd.effects.BlobEmitter;
import pd.effects.particles.ShaftParticle;
import pd.messages.Messages;

/** Permanent ground light placed by the legacy torch SET action. */
public class TorchLight extends Blob {
	@Override protected void evolve() {
		int width = Dungeon.level.width();
		for (int cell = width + 1; cell < Dungeon.level.length() - width - 1; cell++) {
			off[cell] = cur[cell] > 0 ? cur[cell] : 0;
			volume += off[cell];
		}
	}
	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(ShaftParticle.FACTORY, 1f, 0);
	}
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
