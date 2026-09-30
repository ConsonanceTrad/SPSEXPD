package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class UncleDumbbell extends SellItem {
	{ image = ItemSpriteSheet.UNCLE_DUMBBELL; }
	@Override public int value() { return 100 * quantity; }
}
