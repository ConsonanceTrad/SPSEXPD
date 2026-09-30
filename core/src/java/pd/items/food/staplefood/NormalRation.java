/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.staplefood;

import pd.sprites.ItemSpriteSheet;

public class NormalRation extends StapleFood {
	{
		image = ItemSpriteSheet.RATION;
		energy = 300f;
	}
	@Override public int value() { return 5 * quantity; }
}
