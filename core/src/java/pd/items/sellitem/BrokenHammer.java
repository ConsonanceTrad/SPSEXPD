package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class BrokenHammer extends SellItem {
	{ image = ItemSpriteSheet.BROKEN_HAMMER; }
	@Override public int value() { return 30 * quantity; }
}
