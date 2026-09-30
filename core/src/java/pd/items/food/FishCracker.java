/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food;

import pd.sprites.ItemSpriteSheet;

public class FishCracker extends Food {
	{
		image = ItemSpriteSheet.SPS_FISH_FOOD;
		energy = 200f;
		hornValue = 0;
	}
	@Override public int value() { return 1000 * quantity; }
}
