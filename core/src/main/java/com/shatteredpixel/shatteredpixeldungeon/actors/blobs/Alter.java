package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

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
