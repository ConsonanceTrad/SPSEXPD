package com.shatteredpixel.shatteredpixeldungeon.items.reward;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Blackberry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Blueberry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Cloudberry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Moonberry;

public class CaveReward extends ChallengeReward {
	public CaveReward() { super(0xFFFFFF); }
	@Override protected Item[] contents() {
		return new Item[]{new Moonberry().quantity(10), new Cloudberry().quantity(10),
				new Blueberry().quantity(10), new Blackberry().quantity(10)};
	}
}
