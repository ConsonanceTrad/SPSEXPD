/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.sellitem;

import pd.sprites.ItemSpriteSheet;

public class MiniBunny extends SellItem {
	{
		image = ItemSpriteSheet.RABBIT_PET_EGG;
		stackable = true;
	}
	@Override public int value() { return 100 * quantity; }
	@Override public String info() { return desc(); }
}
