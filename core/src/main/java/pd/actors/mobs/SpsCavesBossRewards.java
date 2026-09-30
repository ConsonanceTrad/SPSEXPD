/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Badges;
import pd.Dungeon;
import pd.items.Item;
import pd.items.SkillBook;
import pd.items.journalpages.JournalPage;
import pd.items.journalpages.Sokoban3;
import pd.items.keys.SpsSkeletonKey;
import pd.items.keys.WornKey;
import pd.items.misc.LuckyBadge;
import pd.scenes.GameScene;
import render.utils.math.Random;

final class SpsCavesBossRewards {
	private SpsCavesBossRewards() { }

	static void grant(int pos, Item rareLoot, Item commonLoot) {
		if (Dungeon.level == null || !Dungeon.level.locked) return;
		Dungeon.level.unseal();
		GameScene.bossSlain();
		Badges.validateBossSlain();

		Dungeon.level.drop(new SkillBook(), pos).sprite.drop();
		Dungeon.level.drop(new SpsSkeletonKey(Dungeon.depth), pos).sprite.drop();
		Dungeon.level.drop(new WornKey(Dungeon.depth), pos).sprite.drop();
		float rareChance = LegacyDualLootMob.primaryShare(0.2f, 1f,
				LuckyBadge.luckBonus(Dungeon.hero));
		Item bossLoot = Random.Float() < rareChance ? rareLoot : commonLoot;
		if (bossLoot != null) Dungeon.level.drop(bossLoot, pos).sprite.drop();
		JournalPage.dropAt(new Sokoban3(), pos);
	}
}
