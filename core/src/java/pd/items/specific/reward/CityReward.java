package pd.items.specific.reward;

import pd.items.Crystalnucleus;
import pd.items.Item;

public class CityReward extends ChallengeReward {
	public CityReward() { super(0xFFFF44); }
	@Override protected Item[] contents() {
		return new Item[]{new Crystalnucleus(), new Crystalnucleus(), new Crystalnucleus(),
				new Crystalnucleus(), new Crystalnucleus()};
	}
}
