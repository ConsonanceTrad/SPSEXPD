package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class Simple360 extends SellItem {
	{ image = ItemSpriteSheet.SIMPLE_360; }
	@Override public int value() { return 80 * quantity; }
}
