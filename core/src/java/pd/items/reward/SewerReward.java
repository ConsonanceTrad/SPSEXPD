package pd.items.reward;

import pd.items.Item;
import pd.items.StoneOre;

public class SewerReward extends ChallengeReward {
	public SewerReward() { super(0x000000); }
	@Override protected Item[] contents() { return new Item[]{new StoneOre(20)}; }
}
