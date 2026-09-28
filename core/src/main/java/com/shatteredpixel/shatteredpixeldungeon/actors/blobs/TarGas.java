/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Tar;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

/** Flammable SPS oil mist which coats occupants in tar. */
public class TarGas extends Blob {
	@Override
	protected void evolve() {
		super.evolve();
		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * Dungeon.level.width();
				if (cur[cell] <= 0) continue;
				Char ch = Actor.findChar(cell);
				if (ch != null && !ch.isImmune(getClass())) {
					Buff.affect(ch, Tar.class);
					if (ch.buff(Burning.class) != null) GameScene.add(Blob.seed(cell, 2, Fire.class));
				}
			}
		}
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(Speck.factory(Speck.STENCH), 0.6f);
	}

	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
