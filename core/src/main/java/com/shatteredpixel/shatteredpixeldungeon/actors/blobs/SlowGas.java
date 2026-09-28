/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpeedSlow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.StandDown;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class SlowGas extends Blob {

	@Override
	protected void evolve() {
		int width = Dungeon.level.width();
		int left = Math.max(1, area.left - 1);
		int right = Math.min(Dungeon.level.width() - 2, area.right);
		int top = Math.max(1, area.top - 1);
		int bottom = Math.min(Dungeon.level.height() - 2, area.bottom);
		for (int x = left; x <= right; x++) {
			for (int y = top; y <= bottom; y++) {
				int cell = x + y * width;
				if (cur[cell] > 0) {
					affectCell(cell);
					off[cell] = cur[cell] - 1;
					volume += off[cell];
				} else {
					off[cell] = 0;
				}
			}
		}
	}

	private void affectCell(int cell) {
		Char target = Actor.findChar(cell);
		if (target != null && !target.isImmune(getClass())) {
			if (target.buff(StandDown.class) != null) {
				Buff.prolong(target, StandDown.class, 2f);
			} else {
				SpeedSlow slow = Buff.prolong(target, SpeedSlow.class, 3f);
				if (slow.cooldown() >= 10f) Buff.prolong(target, StandDown.class, 5f);
			}
		}
		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap != null) heap.icehit();
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(SnowParticle.FACTORY, 0.05f, 0);
	}
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
