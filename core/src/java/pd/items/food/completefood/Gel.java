/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.sprites.ItemSprite;

public class Gel extends CompleteFood {
	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0000FF);
	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 10f; }
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
	@Override public int value() { return 50 * quantity; }
}
