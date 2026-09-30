package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class HunterLens extends SellItem {
	{ image = ItemSpriteSheet.DEWDROP; }
	@Override public int value() { return 500 * quantity; }
}
