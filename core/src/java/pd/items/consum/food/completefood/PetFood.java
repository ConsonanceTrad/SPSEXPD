/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumUsefulUsefulDict;


public class PetFood extends CompleteFood {
	{
		image = ConsumUsefulUsefulDict.PET_FOOD;
		energy = 10f;
	}
	@Override public int value() { return quantity; }
}
