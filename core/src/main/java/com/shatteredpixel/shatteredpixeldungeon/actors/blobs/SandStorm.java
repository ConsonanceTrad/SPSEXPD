/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dry;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SandParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

/** Short-lived SPS sand cloud which dries actors and earth-hits floor items. */
public class SandStorm extends Blob {
	@Override
	protected void evolve() {
		int width = Dungeon.level.width();
		for (int x = Math.max(1, area.left); x < Math.min(width - 1, area.right); x++) {
			for (int y = Math.max(1, area.top); y < Math.min(Dungeon.level.height() - 1, area.bottom); y++) {
				int cell = x + y * width;
				int value = cur[cell] > 0 ? cur[cell] - 1 : 0;
				off[cell] = value;
				if (cur[cell] <= 0) continue;
				Char ch = Actor.findChar(cell);
				if (ch != null && !ch.isImmune(getClass())) Buff.affect(ch, Dry.class, 4f);
				Heap heap = Dungeon.level.heaps.get(cell);
				if (heap != null) heap.earthhit();
				volume += value;
			}
		}
	}

	@Override
	public void seed(Level level, int cell, int amount) {
		if (cur == null) cur = new int[level.length()];
		if (off == null) off = new int[cur.length];
		if (cur[cell] != 0) return;
		cur[cell] = amount;
		volume += amount;
		area.union(cell % level.width(), cell / level.width());
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(SandParticle.FACTORY, 0.5f, 0);
	}
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
