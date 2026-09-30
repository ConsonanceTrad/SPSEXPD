package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class HummingTool extends SellItem {
	{ image = ItemSpriteSheet.HUMMING_TOOL; }
	@Override public int value() { return 120 * quantity; }
}
