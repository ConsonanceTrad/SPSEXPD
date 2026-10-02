package pd.levels.traps;

import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.VenomGas;
import pd.items.Heap;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class VenomTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(VenomTrap.class)
			.t("name", "猛毒陷阱")
			.t("desc", "触发这个陷阱将在附近释放出一片致命的猛毒气体。");
	}



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
