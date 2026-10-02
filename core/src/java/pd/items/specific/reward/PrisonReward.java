package pd.items.specific.reward;

import pd.items.Item;
import pd.items.consum.food.fruit.FullMoonberry;

public class PrisonReward extends ChallengeReward {
	public PrisonReward() { super(0x0000FF); }
	@Override protected Item[] contents() { return new Item[]{new FullMoonberry()}; }
}
