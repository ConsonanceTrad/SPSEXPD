package pd.items.consum.food;

import pd.atlas.items.ConsumUsefulUsefulDict;
public class PetFood extends Food {
	{ image = ConsumUsefulUsefulDict.PET_FOOD; energy = 10f; }
	@Override public int value() { return quantity; }
}
