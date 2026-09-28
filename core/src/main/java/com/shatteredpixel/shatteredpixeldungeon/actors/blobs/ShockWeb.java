/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShockWebParticle;

/** The non-spreading electric trail used by the original boss-rush machines. */
public class ShockWeb extends Blob {

	@Override
	protected void evolve() {
		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * Dungeon.level.width();
				off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;
				volume += off[cell];
				Char ch = Actor.findChar(cell);
				if (cur[cell] > 0 && ch != null && !ch.isImmune(getClass())) {
					ch.damage(5, this);
					Buff.prolong(ch, Paralysis.class, 1f);
				}
			}
		}
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(ShockWebParticle.FACTORY, 0.4f);
	}
}
