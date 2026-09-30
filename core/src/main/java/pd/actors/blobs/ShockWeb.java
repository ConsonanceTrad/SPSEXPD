/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.effects.BlobEmitter;
import pd.effects.particles.ShockWebParticle;

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
