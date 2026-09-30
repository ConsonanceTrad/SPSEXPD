package pd.levels.traps;

import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.VenomGas;
import pd.items.Heap;
import pd.scenes.GameScene;

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
