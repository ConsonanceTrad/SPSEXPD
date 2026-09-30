package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class CrossPhoto extends SellItem {
	{ image = ItemSpriteSheet.CROSS_PHOTO; }
	@Override public int value() { return 150 * quantity; }
}
