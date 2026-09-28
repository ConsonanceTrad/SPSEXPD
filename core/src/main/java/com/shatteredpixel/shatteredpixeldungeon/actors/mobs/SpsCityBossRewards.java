/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.ArmorKit;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.LuckyBadge;
import com.shatteredpixel.shatteredpixeldungeon.items.journalpages.JournalPage;
import com.shatteredpixel.shatteredpixeldungeon.items.journalpages.Sokoban4;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SpsSkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.WornKey;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

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
