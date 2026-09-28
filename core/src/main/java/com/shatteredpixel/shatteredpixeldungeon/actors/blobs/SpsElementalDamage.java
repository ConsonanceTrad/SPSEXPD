/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Random;

public abstract class SpsElementalDamage extends Blob {
	protected void affectHeap(Heap heap) { }
	protected abstract Object damageSource();

	@Override protected void evolve() {
		int width = Dungeon.level.width();
		int height = Dungeon.level.height();
		int left = Math.max(1, area.left - 1);
		int right = Math.min(width - 2, area.right);
		int top = Math.max(1, area.top - 1);
		int bottom = Math.min(height - 2, area.bottom);
		for (int x = left; x <= right; x++) for (int y = top; y <= bottom; y++) {
			int cell = x + y * width;
			if (cur[cell] > 0) {
				hit(cell);
				off[cell] = cur[cell] - 1;
				volume += off[cell];
			} else off[cell] = 0;
		}
	}

	private void hit(int cell) {
		Char ch = Actor.findChar(cell);
		if (ch != null && !ch.isImmune(getClass())) {
			int low = ch.HP / 100;
			int high = ch.HP / 50;
			ch.damage(Math.max(1, Random.IntRange(low, Math.max(low, high))), damageSource());
		}
		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap != null) affectHeap(heap);
	}

	@Override public String tileDesc() { return Messages.get(this, "desc"); }

	@Override
	public void seed(Level level, int cell, int amount) {
		if (cur == null) cur = new int[level.length()];
		if (off == null) off = new int[cur.length];
		if (cur[cell] == 0) {
			cur[cell] = amount;
			volume += amount;
			area.union(cell % level.width(), cell / level.width());
		}
	}

	/** Compatibility aliases for saves produced before the legacy top-level classes were restored. */
	@Deprecated public static class Ice extends com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.IceEffectDamage { }
	@Deprecated public static class Fire extends com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.FireEffectDamage { }
	@Deprecated public static class Shock extends com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.ShockEffectDamage { }
	@Deprecated public static class Earth extends com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.EarthEffectDamage { }
	@Deprecated public static class Light extends com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.LightEffectDamage { }
	@Deprecated public static class Dark extends com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.DarkEffectDamage { }
}
