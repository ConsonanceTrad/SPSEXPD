package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class Mirror2 extends SellItem {
	{ image = ItemSpriteSheet.MIRROR_2; }
	@Override public int value() { return 112 * quantity; }
}
