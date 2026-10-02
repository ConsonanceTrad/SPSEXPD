/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.bags;

import pd.atlas.items.EquipmentBagsDict;

import pd.items.Item;
import pd.items.StoneOre;
import pd.items.nornstone.NornStone;
import pd.plants.Plant;

/** The original thirty-slot SPS seed, ore, and Norn-stone pouch. */
public class SeedPouch extends Bag {
	{
		image = EquipmentBagsDict.POUCH;
	}
	@Override public boolean canHold(Item item) {
		return (item instanceof Plant.Seed || item instanceof StoneOre || item instanceof NornStone)
				&& super.canHold(item);
	}
	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
