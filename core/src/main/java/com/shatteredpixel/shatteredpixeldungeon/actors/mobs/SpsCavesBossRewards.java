/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.SkillBook;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.LuckyBadge;
import com.shatteredpixel.shatteredpixeldungeon.items.journalpages.JournalPage;
import com.shatteredpixel.shatteredpixeldungeon.items.journalpages.Sokoban3;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SpsSkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.WornKey;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

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
