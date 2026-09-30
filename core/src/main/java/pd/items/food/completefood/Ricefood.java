/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.sprites.ItemSpriteSheet;

public class Ricefood extends CompleteFood {
	{ image = ItemSpriteSheet.RICE_FOOD; energy = 450f; }
	@Override public int value() { return 3 * quantity; }
}
