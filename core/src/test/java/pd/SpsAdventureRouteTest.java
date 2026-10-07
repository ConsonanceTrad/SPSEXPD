package pd;

import pd.actors.mobs.Mob;
import pd.items.quest.AdventureJournal;
import pd.levels.BossRushLevel;
import pd.levels.ChaosLevel;
import pd.levels.DeadEndLevel;
import pd.levels.DragonCaveLevel;
import pd.levels.FieldBossLevel;
import pd.levels.FishingBossLevel;
import pd.levels.InfestBossLevel;
import pd.levels.Level;
import pd.levels.MinesBossLevel;
import pd.levels.NewRoomLevel;
import pd.levels.PotLevel;
import pd.levels.RoomOfZotLevel;
import pd.levels.SafeLevel;
import pd.levels.ShadowEaterLevel;
import pd.levels.SkeletonBossLevel;
import pd.levels.SokobanCastle;
import pd.levels.SokobanIntroLevel;
import pd.levels.SokobanPuzzlesLevel;
import pd.levels.SokobanTeleportLevel;
import pd.levels.SpringFestivalLevel;
import pd.levels.TenguDenLevel;
import pd.levels.ThiefBossLevel;
import pd.levels.ThiefCatchLevel;
import pd.levels.TownLevel;
import render.noosa.Game;

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

		//SPSEXPD: 紧急回退只在调试构建下提供（正式游戏必须走关卡内出口）
		Dungeon.branch = AdventureJournal.branchFor(22);
		check(!new pd.items.DolyaSlate().actions(null).contains(AdventureJournal.AC_RETURN),
				"非调试构建下异界分支仍提供紧急回退");
		String version = Game.version;
		Game.version = "0.1.3-INDEV";
		check(new pd.items.DolyaSlate().actions(null).contains(AdventureJournal.AC_RETURN),
				"调试构建下异界分支不再提供紧急回退");
		Game.version = version;

		//SPSEXPD: 无尽模式（混沌层）里禁止一切跨层传送/返回（石板与传送道具同源共用该判定）
		Level chaos = Dungeon.createAdventureLevel(22);
		check(chaos instanceof ChaosLevel, "异界目的地22不再是混沌层");
		Level previousLevel = Dungeon.level;
		Dungeon.level = chaos;
		check(!Dungeon.interfloorTeleportAllowed(), "无尽模式里仍允许跨层传送或返回");
		Dungeon.level = previousLevel;
		Dungeon.branch = 0;
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
