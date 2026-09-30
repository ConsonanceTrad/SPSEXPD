/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.sprites.ItemSpriteSheet;

public class PetFood extends CompleteFood {
	{
		image = ItemSpriteSheet.PET_FOOD;
		energy = 10f;
	}
	@Override public int value() { return quantity; }
}
