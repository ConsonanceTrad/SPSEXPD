/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.Statistics;
import pd.items.AncientCoin;
import pd.items.Bone;
import pd.items.ConchShell;
import pd.items.Heap;
import pd.items.Item;
import pd.items.SacrificeBook;
import pd.items.TreasureMap;
import pd.items.challengelists.ChallengePageDrops;
import pd.items.challengelists.CityChallenge;
import pd.items.challengelists.PrisonChallenge;
import pd.items.challengelists.SewerChallenge;
import pd.items.food.GoldenNut;
import pd.items.reward.CaveReward;
import pd.items.reward.CityReward;
import pd.items.reward.PrisonReward;
import pd.items.reward.SewerReward;

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
