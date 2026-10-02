/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Dungeon;
import pd.effects.BlobEmitter;
import pd.effects.particles.ShaftParticle;
import pd.messages.Messages;
import pd.messages.InlineText;

/** Permanent ground light placed by the legacy torch SET action. */
public class TorchLight extends Blob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TorchLight.class)
			.t("desc", "火把的光芒照亮了周围的环境。");
	}

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
