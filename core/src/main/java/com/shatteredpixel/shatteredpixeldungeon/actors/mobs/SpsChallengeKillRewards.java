/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.items.AncientCoin;
import com.shatteredpixel.shatteredpixeldungeon.items.Bone;
import com.shatteredpixel.shatteredpixeldungeon.items.ConchShell;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.SacrificeBook;
import com.shatteredpixel.shatteredpixeldungeon.items.TreasureMap;
import com.shatteredpixel.shatteredpixeldungeon.items.challengelists.ChallengePageDrops;
import com.shatteredpixel.shatteredpixeldungeon.items.challengelists.CityChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.challengelists.PrisonChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.challengelists.SewerChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.food.GoldenNut;
import com.shatteredpixel.shatteredpixeldungeon.items.reward.CaveReward;
import com.shatteredpixel.shatteredpixeldungeon.items.reward.CityReward;
import com.shatteredpixel.shatteredpixeldungeon.items.reward.PrisonReward;
import com.shatteredpixel.shatteredpixeldungeon.items.reward.SewerReward;

public final class SpsChallengeKillRewards {
	private SpsChallengeKillRewards() { }

	public static void gnollArcher(int pos) {
		if (Dungeon.branch == 0) ChallengePageDrops.offer(new SewerChallenge(), pos);
		milestone(Statistics.gnollArchersKilled, new TreasureMap(), new SewerReward(), pos);
	}

	public static void mossySkeleton(int pos) {
		if (Dungeon.branch == 0) ChallengePageDrops.offer(new PrisonChallenge(), pos);
		milestone(Statistics.mossySkeletonsKilled, new Bone(), new PrisonReward(), pos);
	}

	public static void albinoPiranha(int pos) {
		milestone(Statistics.albinoPiranhasKilled, new ConchShell(), new CaveReward(), pos);
	}

	public static void goldThief(int pos) {
		if (Dungeon.branch == 0) ChallengePageDrops.offer(new CityChallenge(), pos);
		milestone(Statistics.goldThievesKilled, new AncientCoin(), new CityReward(), pos);
	}

	private static void milestone(int kills, Item token, Item reward, int pos) {
		if (kills == 25) drop(token, pos);
		if (kills == 50) drop(new SacrificeBook(), pos);
		if (kills == 100) {
			drop(reward, pos);
			if (Statistics.gnollArchersKilled >= 100 && Statistics.mossySkeletonsKilled >= 100
					&& Statistics.albinoPiranhasKilled >= 100 && Statistics.goldThievesKilled >= 100) {
				drop(new GoldenNut(), pos);
			}
		}
	}

	private static void drop(Item item, int pos) {
		Heap heap = Dungeon.level.drop(item, pos);
		if (heap.sprite != null) heap.sprite.drop();
	}
}
