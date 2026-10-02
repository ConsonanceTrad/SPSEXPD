/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.specific.sellitem;

import pd.atlas.items.EquipmentJewelleryArtifactDict;


public class LingHeart extends SellItem {
	{
		image = EquipmentJewelleryArtifactDict.LING_HEART_0;
		stackable = true;
	}
	@Override public int value() { return 100000 * quantity; }
	@Override public String info() { return desc(); }
}
