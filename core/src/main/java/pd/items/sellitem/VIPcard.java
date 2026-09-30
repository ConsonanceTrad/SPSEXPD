/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.sellitem;

import pd.sprites.ItemSpriteSheet;

public class VIPcard extends SellItem {
	{
		image = ItemSpriteSheet.VIP_CARD;
		stackable = true;
	}
	@Override public int value() { return 400 * quantity; }
	@Override public String info() { return desc(); }
}
