package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.watabou.noosa.Game;

/** Verifies the SPS-PD 0.9.8 ranking depth and score formula. */
public final class SpsRankingsTest {
	private SpsRankingsTest() { }

	public static void main(String[] args) {
		Game.version = "test";
		Dungeon.hero = new Hero();
		Dungeon.hero.lvl = 12;
		Dungeon.depth = 14;
		Dungeon.branch = AdventureJournal.branchFor(22);
		Statistics.goldCollected = 345;

		check(Rankings.currentRecordDepth() == 85,
				"混沌路线的排行榜楼层没有使用旧版有效深度85");
		check(Dungeon.displayDepth() == 85,
				"混沌路线界面没有显示旧版有效深度85");
		check(Rankings.spsScore(345, 12, 85, false) == 102345,
				"失败分数不符合旧版金币与有效深度公式");
		check(Rankings.spsScore(345, 12, 85, true) == 240690,
				"胜利分数没有按旧版100层基数和双倍结算");

		Statistics.gameWon = false;
		check(Rankings.INSTANCE.calculateScore() == 102345,
				"排行榜结算没有使用旧版失败分数");
		check(Statistics.progressScore == 102000
				&& Statistics.treasureScore == 345
				&& Statistics.exploreScore == 0
				&& Statistics.totalBossScore == 0
				&& Statistics.totalQuestScore == 0
				&& Statistics.winMultiplier == 1
				&& Statistics.chalMultiplier == 1,
				"排行榜分项没有同步旧版评分结果");

		Statistics.gameWon = true;
		check(Rankings.INSTANCE.calculateScore() == 240690
				&& Statistics.progressScore == 120000
				&& Statistics.winMultiplier == 2,
				"排行榜胜利结算没有同步旧版公式");

		Dungeon.branch = 0;
		Dungeon.depth = 19;
		check(Rankings.currentRecordDepth() == 19,
				"主线排行榜楼层被支线有效深度映射污染");
		check(Dungeon.displayDepth() == 19,
				"主线界面楼层被支线有效深度映射污染");
		System.out.println("SPS排行榜测试通过：记录楼层、失败与胜利分数及界面分项均使用0.9.8公式。");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
