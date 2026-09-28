/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class HealLight extends Blob {
	@Override
	protected void evolve() {
		int width = Dungeon.level.width();
		int height = Dungeon.level.height();
		for (int y = Math.max(1, area.top - 1); y <= Math.min(height - 2, area.bottom); y++) {
			for (int x = Math.max(1, area.left - 1); x <= Math.min(width - 2, area.right); x++) {
				int cell = x + y * width;
				if (cur[cell] <= 0) {
					off[cell] = 0;
					continue;
				}
				Char ch = Actor.findChar(cell);
				if (ch != null && !ch.isImmune(getClass()) && ch.HP < ch.HT) {
					ch.HP = Math.min(ch.HT, ch.HP + Math.max(1, ch.HT / 25));
					if (ch.sprite != null) ch.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
				}
				Heap heap = Dungeon.level.heaps.get(cell);
				if (heap != null) heap.lighthit();
				off[cell] = cur[cell] - 1;
				volume += off[cell];
			}
		}
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(ShaftParticle.FACTORY, 1f, 0);
	}

	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
