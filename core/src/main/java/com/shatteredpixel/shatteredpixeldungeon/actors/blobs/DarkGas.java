/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.DarkLightParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

/** SPS darkness cloud which repeatedly blinds occupants. */
public class DarkGas extends Blob {
	@Override
	protected void evolve() {
		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * Dungeon.level.width();
				int value = cur[cell] > 0 ? cur[cell] - 1 : 0;
				off[cell] = value;
				if (value <= 0) continue;
				volume += value;
				Char ch = Actor.findChar(cell);
				if (ch != null && !ch.isImmune(getClass())) Buff.prolong(ch, Blindness.class, 3f);
			}
		}
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(DarkLightParticle.FACTORY, 0.8f, 0);
	}

	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
