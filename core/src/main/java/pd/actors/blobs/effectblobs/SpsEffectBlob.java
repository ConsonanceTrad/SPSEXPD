package pd.actors.blobs.effectblobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.items.Heap;

/** Shared bounded evolution for the six short-lived SPS elemental fields. */
abstract class SpsEffectBlob extends Blob {

	@Override
	protected void evolve() {
		int width = Dungeon.level.width();
		int height = Dungeon.level.height();
		int left = Math.max(1, area.left - 1);
		int right = Math.min(width - 2, area.right);
		int top = Math.max(1, area.top - 1);
		int bottom = Math.min(height - 2, area.bottom);
		for (int y = top; y <= bottom; y++) {
			for (int x = left; x <= right; x++) {
				int cell = x + y * width;
				if (cur[cell] > 0) {
					Char target = Actor.findChar(cell);
					if (target != null && !target.isImmune(getClass())) affect(target);
					Heap heap = Dungeon.level.heaps.get(cell);
					if (heap != null) affect(heap);
					off[cell] = cur[cell] - 1;
					volume += off[cell];
				} else {
					off[cell] = 0;
				}
			}
		}
	}

	protected abstract void affect(Char target);
	protected abstract void affect(Heap heap);
}
