package pd.items.specific.reward;

import pd.items.Item;
import pd.items.consum.food.fruit.Blackberry;
import pd.items.consum.food.fruit.Blueberry;
import pd.items.consum.food.fruit.Cloudberry;
import pd.items.consum.food.fruit.Moonberry;

public class CaveReward extends ChallengeReward {
	public CaveReward() { super(0xFFFFFF); }
	@Override protected Item[] contents() {
		return new Item[]{new Moonberry().quantity(10), new Cloudberry().quantity(10),
				new Blueberry().quantity(10), new Blackberry().quantity(10)};
	}
}
