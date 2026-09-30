/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.effects.BlobEmitter;
import pd.effects.Speck;
import pd.levels.Level;
import pd.messages.Messages;
import pd.scenes.GameScene;

/** Three-turn steam warning which ignites as its strength falls from four to one. */
public class SteamWarn extends Blob {

	@Override
	protected void evolve() {
		for (int cell = 0; cell < cur.length; cell++) {
			int next = cur[cell] > 0 ? cur[cell] - 1 : 0;
			off[cell] = next;
			if (next > 0) {
				volume += next;
				if (volume < 2) GameScene.add(Blob.seed(cell, 5, Fire.class));
			}
		}
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(Speck.factory(Speck.STEAM), 0.2f);
	}

	@Override
	public void seed(Level level, int cell, int amount) {
		if (cur == null) cur = new int[level.length()];
		if (off == null) off = new int[cur.length];
		int difference = amount - cur[cell];
		if (difference > 0) {
			cur[cell] = amount;
			volume += difference;
			area.union(cell % level.width(), cell / level.width());
		}
	}

	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
