/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Badges;
import pd.Dungeon;
import pd.items.Item;
import pd.items.SkillBook;
import pd.items.TenguKey;
import pd.items.specific.journalpages.JournalPage;
import pd.items.specific.journalpages.Sokoban2;
import pd.items.specific.keys.SpsSkeletonKey;
import pd.items.specific.keys.WornKey;
import pd.items.misc.LuckyBadge;
import pd.scenes.GameScene;
import render.utils.math.Random;

final class SpsPrisonBossRewards {
	private SpsPrisonBossRewards() { }

	static void grant(int pos, Item rareLoot, Item commonLoot) {
		Dungeon.level.unseal();
		GameScene.bossSlain();
		Badges.validateBossSlain();
		//SPSEXPD: 首领挑战徽章（旧版有、新移植类缺失）
		if (pd.Statistics.qualifiedForBossChallengeBadge) Badges.validateBossChallengeCompleted();

		Dungeon.level.drop(new SkillBook(), pos).sprite.drop();
		Dungeon.level.drop(new TenguKey(), pos).sprite.drop();
		Dungeon.level.drop(new SpsSkeletonKey(Dungeon.depth), pos).sprite.drop();
		Dungeon.level.drop(new WornKey(Dungeon.depth), pos).sprite.drop();
		float rareChance = LegacyDualLootMob.primaryShare(0.2f, 1f,
				LuckyBadge.luckBonus(Dungeon.hero));
		Item bossLoot = Random.Float() < rareChance ? rareLoot : commonLoot;
		if (bossLoot != null) Dungeon.level.drop(bossLoot, pos).sprite.drop();

		JournalPage.dropAt(new Sokoban2(), pos);
	}
}
