package pd.actors.blobs;

import pd.Dungeon;
import pd.effects.BlobEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.equipment.weapon.Weapon;

public class Alter extends Blob {

	@Override
	protected void evolve() {
		for (int cell = 0; cell < cur.length; cell++) {
			off[cell] = cur[cell];
			volume += off[cell];
		}
	}

	public static void transmute(int cell) {
		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap == null) return;
		Weapon result = heap.consecrate();
		if (result != null) Dungeon.level.drop(result, cell).sprite.drop(cell);
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(Speck.factory(Speck.LIGHT), 0.4f, 0);
	}
}
