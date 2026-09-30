package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class BottleFlower extends SellItem {
	{ image = ItemSpriteSheet.BOTTLE_FLOWER; }
	@Override public int value() { return 1000 * quantity; }
}
