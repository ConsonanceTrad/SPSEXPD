package pd.items.sellitem;
import pd.sprites.ItemSpriteSheet;
public class DwarfHammer extends SellItem {
	{ image = ItemSpriteSheet.DWARF_HAMMER; }
	@Override public int value() { return 150 * quantity; }
}
