package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class Tissue extends SellItem {
	{ image = ItemSpriteSheet.TISSUE; }
	@Override public int value() { return 120 * quantity; }
}
