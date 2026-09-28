/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

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
