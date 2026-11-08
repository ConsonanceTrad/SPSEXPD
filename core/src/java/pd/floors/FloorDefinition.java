package pd.floors;

import pd.levels.Level;

/**
 * SPSEXPD: 楼层注册表的条目 —— 主线与每条支线各登记一次。
 *
 * <p>条目声明：branch 号（存档里存的值，不再重新编号）、支线的主线分叉锚点层、
 * 该支线占用的主 depth 范围、生成哪个 Level，以及 boss 层/商店层/跨层传送、
 * 战斗数值借层等语义标记。楼层的创建（Dungeon.newLevel）、类型判定
 * （bossLevel/shopOnLevel/interfloorTeleportAllowed）、数值借层（Dungeon.legacyDepth）
 * 与玩家显示编号全部改为查本表；以后新增支线只需要在这里加一个条目。</p>
 */
public class FloorDefinition {

	/** Level 工厂（自定义接口，避免依赖 java.util.function，兼容项目的 desugar 配置）。 */
	public interface LevelFactory { Level create(); }

	/** 支线战斗数值借层映射；返回值交给 Dungeon.legacyDepth() 统一处理负值回退。 */
	public interface LegacyDepth { int depthFor(int branch); }

	/** branch 号（存档里存的值；0=主线，-1=教程层，其余为支线）。 */
	public final int branch;

	/** 主线分叉锚点层（玩家显示编号「锚点·序号」用）；0 = 动态借层，显示沿用主线数字。 */
	public final int anchorDepth;

	/** 该支线段的主 depth 范围；-1 = 任意（随进入时的主线层借号）。 */
	public final int minDepth;
	public final int maxDepth;

	public final LevelFactory factory;

	/** 支线层是否禁止一切跨层传送/返回（矿洞、宝库与各临时支线均为 true）。 */
	public final boolean noTeleport;

	/** 支线层是否按 boss 层处理（默认 false；主线判定仍走每章 8 层公式）。 */
	public final boolean bossFloor;

	/** 支线层是否按商店/章首层处理（默认 false）。 */
	public final boolean shopFloor;

	/** 支线战斗数值借层（null = 无映射，语义上直接用当前 depth）。 */
	public final LegacyDepth legacy;

	FloorDefinition(int branch, int anchorDepth, int minDepth, int maxDepth,
	                LevelFactory factory, boolean noTeleport, boolean bossFloor,
	                boolean shopFloor, LegacyDepth legacy) {
		this.branch = branch;
		this.anchorDepth = anchorDepth;
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
		this.factory = factory;
		this.noTeleport = noTeleport;
		this.bossFloor = bossFloor;
		this.shopFloor = shopFloor;
		this.legacy = legacy;
	}

	public boolean isMainline() {
		return branch == 0;
	}

	/** 支线在段内的序号（1 起）；无固定段（minDepth=-1）时按 depth-锚点+1 推算。 */
	public int indexInFloor(int depth) {
		if (minDepth >= 0) return depth - minDepth + 1;
		return depth - anchorDepth + 1;
	}
}
