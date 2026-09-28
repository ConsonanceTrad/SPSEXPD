package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.VenomGas;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class VenomTrap extends Trap {
	{ color = VIOLET; shape = GRILL; }
	@Override public void activate() {
		int legacyDepth = Dungeon.legacyDepth();
		VenomGas gas = Blob.seed(pos, 80 + 5 * legacyDepth, VenomGas.class);
		gas.setStrength(1 + legacyDepth / 4);
		GameScene.add(gas);
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) heap.earthhit();
	}
}
