/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.sprites.ItemSprite;

/** Original red-glowing meat handed out by Xavier251998. */
public class FireMeat extends Food {

	private static final ItemSprite.Glowing RED = new ItemSprite.Glowing(0xCC0000);

	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 150;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return RED;
	}

	@Override
	public int value() {
		return 2 * quantity();
	}
}
