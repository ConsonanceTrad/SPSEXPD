/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.staplefood;

import pd.sprites.ItemSpriteSheet;

public class OverpricedRation extends StapleFood {
	{
		image = ItemSpriteSheet.OVERPRICED;
		energy = 200f;
		hornValue = 2;
	}
	@Override public int value() { return 3 * quantity; }
}
