package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.levels.BossRushLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.ChaosLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.FishingBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.DragonCaveLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.FieldBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.InfestBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MinesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.NewRoomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PotLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.RoomOfZotLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SafeLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.ShadowEaterLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SkeletonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SokobanCastle;
import com.shatteredpixel.shatteredpixeldungeon.levels.SokobanIntroLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SokobanPuzzlesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SokobanTeleportLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SpringFestivalLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.TenguDenLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.ThiefBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.ThiefCatchLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.TownLevel;
import com.watabou.noosa.Game;

/** Verifies every normal journal route and rejects the early fusion placeholder maps. */
public final class SpsAdventureRouteTest {
	private SpsAdventureRouteTest() { }

	public static void main(String[] args) {
		Game.version = "test";
		Class<?>[] expected = {
				SafeLevel.class, SokobanIntroLevel.class, SokobanCastle.class,
				SokobanTeleportLevel.class, SokobanPuzzlesLevel.class, TownLevel.class,
				SpringFestivalLevel.class, MinesBossLevel.class, NewRoomLevel.class,
				InfestBossLevel.class, TenguDenLevel.class, SkeletonBossLevel.class,
				FishingBossLevel.class, ThiefBossLevel.class, FieldBossLevel.class,
				PotLevel.class, ShadowEaterLevel.class, DragonCaveLevel.class,
				ThiefCatchLevel.class, MinesBossLevel.class, RoomOfZotLevel.class,
				BossRushLevel.class, ChaosLevel.class, DeadEndLevel.class, DeadEndLevel.class
		};
		for (int destination = 0; destination < expected.length; destination++) {
			Level level = Dungeon.createAdventureLevel(destination);
			check(level.getClass() == expected[destination],
					"异界目的地" + destination + "路由错误：" + level.getClass().getSimpleName());
		}
		int[] legacyDepths = {
				50, 51, 52, 53, 54, 55, 66, 67, 68,
				35, 36, 37, 38, 40, 43, 45, 47, 39, 41, 67, 59,
				71, 85, -1, -1
		};
		Dungeon.depth = 14;
		for (int destination = 0; destination < legacyDepths.length; destination++) {
			Dungeon.branch = AdventureJournal.branchFor(destination);
			check(AdventureJournal.legacyDepthForBranch(Dungeon.branch) == legacyDepths[destination],
					"异界目的地" + destination + "旧版有效深度错误");
			check(Mob.legacyDungeonDepth() == (legacyDepths[destination] < 0 ? Dungeon.depth : legacyDepths[destination]),
					"异界目的地" + destination + "怪物未使用旧版有效深度");
		}
		Dungeon.branch = 0;
		check(Mob.legacyDungeonDepth() == Dungeon.depth, "主线怪物深度被异界映射污染");
		check(Dungeon.createAdventureLevel(-1) instanceof DeadEndLevel,
				"非法负数目的地没有安全回退");
		check(Dungeon.createAdventureLevel(25) instanceof DeadEndLevel,
				"越界目的地没有安全回退");
		System.out.println("SPS异界路由测试通过：0至22使用旧版专用地图，23、24及非法编号安全返回DeadEndLevel。");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
