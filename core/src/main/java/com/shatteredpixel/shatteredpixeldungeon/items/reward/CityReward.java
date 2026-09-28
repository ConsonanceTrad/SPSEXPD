package com.shatteredpixel.shatteredpixeldungeon.items.reward;

import com.shatteredpixel.shatteredpixeldungeon.items.Crystalnucleus;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public class CityReward extends ChallengeReward {
	public CityReward() { super(0xFFFF44); }
	@Override protected Item[] contents() {
		return new Item[]{new Crystalnucleus(), new Crystalnucleus(), new Crystalnucleus(),
				new Crystalnucleus(), new Crystalnucleus()};
	}
}
