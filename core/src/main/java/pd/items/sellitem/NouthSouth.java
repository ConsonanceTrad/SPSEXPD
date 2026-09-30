package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class NouthSouth extends SellItem {
	{ image = ItemSpriteSheet.NOUTH_SOUTH; }
	@Override public int value() { return 500 * quantity; }
}
