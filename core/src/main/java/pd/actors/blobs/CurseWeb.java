/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.blobs;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShadowCurse;
import pd.effects.BlobEmitter;
import pd.effects.particles.WebParticle;
import pd.levels.Level;
import pd.messages.Messages;

public class CurseWeb extends Blob {

	@Override
	protected void evolve() {
		for (int cell = 0; cell < cur.length; cell++) {
			int remaining = cur[cell] > 0 ? cur[cell] - 1 : 0;
			off[cell] = remaining;
			if (remaining > 0) {
				volume += remaining;
				Char ch = Actor.findChar(cell);
				if (ch != null && !ch.isImmune(getClass())) Buff.affect(ch, ShadowCurse.class);
			}
		}
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.pour(WebParticle.FACTORY, 0.4f);
	}

	@Override
	public void seed(Level level, int cell, int amount) {
		if (cur == null) cur = new int[level.length()];
		if (off == null) off = new int[cur.length];
		int increase = amount - cur[cell];
		if (increase > 0) {
			cur[cell] = amount;
			volume += increase;
			area.union(cell % level.width(), cell / level.width());
		}
	}

	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
