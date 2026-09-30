package pd.items;

import pd.sprites.ItemSpriteSheet;

public class YellowDewdrop extends ColoredDewdrop {
	{ image = ItemSpriteSheet.YELLOW_DEWDROP; }
	@Override protected int baseHealing() { return 5; }
	@Override public int dewValue() { return 5 * quantity; }
}
