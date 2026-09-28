package com.shatteredpixel.shatteredpixeldungeon.items.reward;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.FullMoonberry;

public class PrisonReward extends ChallengeReward {
	public PrisonReward() { super(0x0000FF); }
	@Override protected Item[] contents() { return new Item[]{new FullMoonberry()}; }
}
