package pd.floors;

import java.util.ArrayList;

import pd.Dungeon;
import pd.items.BossRush;
import pd.items.Palantir;
import pd.items.PowerHand;
import pd.items.TreasureMap;
import pd.items.quest.AdventureJournal;
import pd.items.quest.ChallengeJournal;
import pd.levels.BetweenLevel;
import pd.levels.BossRushLevel;
import pd.levels.CaveChallengeLevel;
import pd.levels.CavesLevel;
import pd.levels.ChaosLevel;
import pd.levels.CityChallengeLevel;
import pd.levels.CityLevel;
import pd.levels.DeadEndLevel;
import pd.levels.FieldBossLevel;
import pd.levels.HallsLevel;
import pd.levels.IceChallengeLevel;
import pd.levels.LastLevel;
import pd.levels.MiningLevel;
import pd.levels.PrisonChallengeLevel;
import pd.levels.PrisonLevel;
import pd.levels.SewerChallengeLevel;
import pd.levels.SewerLevel;
import pd.levels.SpsCavesBossLevel;
import pd.levels.SpsCityBossLevel;
import pd.levels.SpsHallsBossLevel;
import pd.levels.SpsPrisonBossLevel;
import pd.levels.SpsSewerBossLevel;
import pd.levels.TriangleCLevel;
import pd.levels.TrianglePLevel;
import pd.levels.TriangleWLevel;
import pd.levels.VaultLevel;
import pd.levels.ZotBossLevel;

/**
 * SPSEXPD: 楼层注册表 —— 全部楼层（主线本体与每条支线）的单点登记。
 *
 * <p>楼层的创建（Dungeon.newLevel）、boss/商店/跨层传送判定与战斗数值借层
 * 全部改为查询本表；玩家显示编号同样以本表为准。branch 号保留各自的
 * 存档数值（主线 0、教程 -1、挑战支线 10..、冒险支线 20..、手工 45..），
 * 旧档无需迁移。</p>
 *
 * <p>新增支线流程见 docs/floor-registry.md。</p>
 */
public final class FloorRegistry {

	private static final ArrayList<FloorDefinition> DEFINITIONS = new ArrayList<>();

	static {
		registerMainline();
		registerQuestBranches();
		registerManualBranches();
	}

	private FloorRegistry() { }

	/** 按当前 depth+branch 解析楼层条目；未登记的 branch 返回 null（调用方回落 DeadEnd/主语义）。 */
	public static FloorDefinition resolve(int depth, int branch) {
		for (FloorDefinition def : DEFINITIONS) {
			if (def.branch != branch) continue;
			if (def.minDepth >= 0 && (depth < def.minDepth || depth > def.maxDepth)) continue;
			return def;
		}
		return null;
	}

	/** 当前分支是否主线（branch 0）。教程层（-1）不作主线。 */
	public static boolean isMainline(int branch) {
		return branch == 0;
	}

	/** 楼层是否按 boss 层处理：主线沿用每章 8 层公式，支线查注册表。 */
	public static boolean isBossFloor(int depth, int branch) {
		if (isMainline(branch)) return mainlineBossFloor(depth);
		FloorDefinition def = resolve(depth, branch);
		return def != null && def.bossFloor;
	}

	/** 楼层是否按商店/章首层处理：主线沿用公式，支线查注册表。 */
	public static boolean isShopFloor(int depth, int branch) {
		if (isMainline(branch)) return mainlineShopFloor(depth);
		FloorDefinition def = resolve(depth, branch);
		return def != null && def.shopFloor;
	}

	/** 楼层是否禁止一切跨层传送/返回：由注册表条目声明（未登记 = 不禁止）。 */
	public static boolean isNoTeleport(int depth, int branch) {
		FloorDefinition def = resolve(depth, branch);
		return def != null && def.noTeleport;
	}

	/** 楼层的战斗数值借层：注册表声明的映射优先，缺省直接用 depth（负值回落 depth）。 */
	public static int legacyDepth(int depth, int branch) {
		FloorDefinition def = resolve(depth, branch);
		if (def == null || def.legacy == null) return depth;
		int legacy = def.legacy.depthFor(branch);
		return legacy < 0 ? depth : legacy;
	}

	/**
	 * SPSEXPD: 玩家显示编号单点 —— 主线（含动态借层的临时支线）保持纯数字；
	 * 有固定分段锚点的支线显示「分叉层·序号」（如矿洞 "17·1"…，用户定稿的紧凑式）。
	 */
	public static String displayLabel(int depth, int branch) {
		if (branch == 0) return Integer.toString(depth);
		FloorDefinition def = resolve(depth, branch);
		if (def == null || def.anchorDepth <= 0) return Integer.toString(depth);
		return def.anchorDepth + "·" + def.indexInFloor(depth);
	}

	/** 楼层的主题（章节）归属：主线按所在 depth；支线按分叉锚点层所在章（供清缓存/加载画面用）。 */
	public static int chapterFor(int depth, int branch) {
		return Dungeon.chapterIndex(anchorOf(depth, branch)) + 1;
	}

	/** 楼层在主线上的锚点层：主线与动态借层支线返回 depth 本身；分段支线返回分叉层。 */
	public static int anchorOf(int depth, int branch) {
		if (branch == 0 || depth <= 0) return Math.max(depth, 0);
		FloorDefinition def = resolve(depth, branch);
		if (def == null || def.anchorDepth <= 0) return Math.max(depth, 0);
		return def.anchorDepth;
	}

	/**
	 * SPSEXPD: 楼层推进单点 —— 无 LevelTransition 的直接跳层（如社会升降器、
	 * 飞轮坠落）统一用这里 ±1。分段支线的进出由各支线 Level 自己的
	 * LevelTransition 定义；推进越出注册表分段时由 Dungeon.newLevel() 的
	 * DeadEnd 兜底（与旧行为一致）。
	 */
	public static int stepDepth(int depth, int branch, boolean down) {
		return down ? depth + 1 : depth - 1;
	}

	// ————————————————————— 登记区 ————————————————————— //

	//主线的 boss/商人层公式（每章 8 层：第 7 层 boss，第 8/16/24/32 层与 0 层为商人层）
	private static boolean mainlineBossFloor(int depth) {
		return depth > 0 && depth % Dungeon.FLOORS_PER_CHAPTER == Dungeon.FLOORS_PER_CHAPTER - 1;
	}

	private static boolean mainlineShopFloor(int depth) {
		return depth == 0
				|| (depth % Dungeon.FLOORS_PER_CHAPTER == 0 && depth < Dungeon.LAST_LEVEL_DEPTH);
	}

	private static void registerMainline() {
		//主线 branch 0：占 0..LAST_LEVEL_DEPTH 全段（0=城镇层，1..40 为地牢），boss/商人判定走公式
		DEFINITIONS.add(new FloorDefinition(0, 0, 0, Dungeon.LAST_LEVEL_DEPTH,
				() -> {
					//SPS: 0 层 = 特殊初始层（学者+商店安全层）；每章 8 层按 6 普通 + boss + 商人层
					switch (Dungeon.depth) {
						case 0:
							return new BetweenLevel();
						case 1: case 2: case 3: case 4: case 5: case 6:
							return new SewerLevel();        // 章 1 普通
						case 7:
							return new SpsSewerBossLevel();
						case 8:
							return new BetweenLevel();      // 章 1 商人层
						case 9: case 10: case 11: case 12: case 13: case 14:
							return new PrisonLevel();
						case 15:
							return new SpsPrisonBossLevel();
						case 16:
							return new BetweenLevel();
						case 17: case 18: case 19: case 20: case 21: case 22:
							return new CavesLevel();
						case 23:
							return new SpsCavesBossLevel();
						case 24:
							return new BetweenLevel();
						case 25: case 26: case 27: case 28: case 29: case 30:
							return new CityLevel();
						case 31:
							return new SpsCityBossLevel();
						case 32:
							return new BetweenLevel();
						case 33: case 34: case 35: case 36: case 37: case 38:
							return new HallsLevel();
						case 39:
							return new SpsHallsBossLevel();
						case Dungeon.LAST_LEVEL_DEPTH:
							return new LastLevel();
						default:
							return new DeadEndLevel();
					}
				},
				false, false, false, null));
	}

	private static void registerQuestBranches() {
		//挑战支线：每条一个 branch（10..），锚点层与数值借层由 ChallengeJournal 提供
		for (int i = 0; i < ChallengeJournal.CHALLENGE_COUNT; i++) {
			final int challenge = i;
			DEFINITIONS.add(new FloorDefinition(
					ChallengeJournal.FIRST_BRANCH + i,
					ChallengeJournal.anchorDepth(challenge), -1, -1,
					challengeFactory(challenge),
					true, false, false,
					ChallengeJournal::legacyDepthForBranch));
		}

		//冒险支线：每条目的地一个 branch（20..），创建走 Dungeon.createAdventureLevel 路由表
		for (int d = 0; d < AdventureJournal.DESTINATION_COUNT; d++) {
			final int destination = d;
			DEFINITIONS.add(new FloorDefinition(
					AdventureJournal.FIRST_BRANCH + d,
					AdventureJournal.anchorDepth(d), -1, -1,
					() -> Dungeon.createAdventureLevel(destination),
					true, false, false,
					AdventureJournal::legacyDepthForBranch));
		}
	}

	private static FloorDefinition.LevelFactory challengeFactory(final int challenge) {
		switch (challenge) {
			case 0: return () -> new SewerChallengeLevel();
			case 1: return () -> new PrisonChallengeLevel();
			case 2: return () -> new CaveChallengeLevel();
			case 3: return () -> new CityChallengeLevel();
			case 4: return () -> new IceChallengeLevel();
			case 5: return () -> new TriangleCLevel();
			case 6: return () -> new TrianglePLevel();
			case 7: return () -> new TriangleWLevel();
			default: return () -> new DeadEndLevel();
		}
	}

	private static void registerManualBranches() {
		//矿洞（branch 1，借主 depth 17..22）与宝库（branch 1，借主 depth 25..30）：同支线的两个分段
		DEFINITIONS.add(new FloorDefinition(1, 17, 17, 22, MiningLevel::new, true, false, false, null));
		DEFINITIONS.add(new FloorDefinition(1, 25, 25, 30, VaultLevel::new, true, false, false, null));

		//临时单层支线：锚点=0（动态借层，显示沿用主线数字，与旧行为一致）
		DEFINITIONS.add(new FloorDefinition(BossRush.BRANCH, 0, -1, -1, BossRushLevel::new, true, false, false, null));
		DEFINITIONS.add(new FloorDefinition(TreasureMap.BRANCH, 0, -1, -1, FieldBossLevel::new, true, false, false, null));
		DEFINITIONS.add(new FloorDefinition(PowerHand.CHAOS_BRANCH, 0, -1, -1, ChaosLevel::new, true, false, false, null));
		DEFINITIONS.add(new FloorDefinition(Palantir.BRANCH, 0, -1, -1, ZotBossLevel::new, true, false, false, null));
	}
}
