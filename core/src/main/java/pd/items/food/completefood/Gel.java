/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;

public class Gel extends CompleteFood {
	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0000FF);
	{ image = ItemSpriteSheet.GEL; energy = 10f; }
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
	@Override public int value() { return 50 * quantity; }
}
