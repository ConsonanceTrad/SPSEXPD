/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;


public class Ricefood extends CompleteFood {
	{ image = ConsumFoodFoodDict.RICE_FOOD; energy = 450f; }
	@Override public int value() { return 3 * quantity; }
}
