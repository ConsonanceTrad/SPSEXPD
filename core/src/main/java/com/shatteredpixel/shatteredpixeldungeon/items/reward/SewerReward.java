package com.shatteredpixel.shatteredpixeldungeon.items.reward;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;

public class SewerReward extends ChallengeReward {
	public SewerReward() { super(0x000000); }
	@Override protected Item[] contents() { return new Item[]{new StoneOre(20)}; }
}
