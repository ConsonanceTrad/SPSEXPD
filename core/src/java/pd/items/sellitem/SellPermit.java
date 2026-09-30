package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class SellPermit extends SellItem {
	{ image = ItemSpriteSheet.SELL_PERMIT; }
	@Override public int value() { return 50 * quantity; }
}
