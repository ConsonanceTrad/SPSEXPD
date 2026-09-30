/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Badges;
import pd.Dungeon;
import pd.items.ArmorKit;
import pd.items.Gold;
import pd.items.Item;
import pd.items.misc.LuckyBadge;
import pd.items.journalpages.JournalPage;
import pd.items.journalpages.Sokoban4;
import pd.items.keys.SpsSkeletonKey;
import pd.items.keys.WornKey;
import pd.scenes.GameScene;
import render.utils.Random;

final class SpsCityBossRewards {
	private SpsCityBossRewards() { }

	static void grant(int pos, int minGold, int maxGold, Item rareLoot, Item commonLoot) {
		if (Dungeon.level == null || !Dungeon.level.locked) return;
		Dungeon.level.unseal();
		GameScene.bossSlain();
		Badges.validateBossSlain();
		Dungeon.level.drop(new ArmorKit(), pos).sprite.drop();
		Dungeon.level.drop(new SpsSkeletonKey(Dungeon.depth), pos).sprite.drop();
		Dungeon.level.drop(new WornKey(Dungeon.depth), pos).sprite.drop();
		Dungeon.level.drop(new Gold(Random.Int(minGold, maxGold)), pos).sprite.drop();
		float rareChance = LegacyDualLootMob.primaryShare(0.2f, 1f,
				LuckyBadge.luckBonus(Dungeon.hero));
		Item bossLoot = Random.Float() < rareChance ? rareLoot : commonLoot;
		if (bossLoot != null) Dungeon.level.drop(bossLoot, pos).sprite.drop();
		JournalPage.dropAt(new Sokoban4(), pos);
	}
}
