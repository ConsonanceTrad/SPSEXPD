/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.plants;

import pd.messages.InlineText;

/**
 * 兼容壳：破碎 4.0 的 Mageroyal（魔皇草）与 SPS-PD 的 {@link Dreamfoil}（梦叶花）是同一种植物，
 * 融合项目时未合并。现已合并到 {@link Dreamfoil}：本类只为读取旧存档里可能存在的
 * {@code pd.plants.Mageroyal} / {@code pd.plants.Mageroyal$Seed} 实例而保留，
 * 不再进入种子生成池、炼药配方、飞镖涂抹表与图鉴。
 *
 * @deprecated 使用 {@link Dreamfoil}
 */
@Deprecated
public class Mageroyal extends Dreamfoil {
	static {
		InlineText.of(Mageroyal.class)
			.t("name", "梦叶花")
			.t("refreshed", "你感觉浑身清爽。")
			.t("desc", "梦叶花含有强力中和成分。它会净化英雄、令其他生物陷入魔法睡眠。")
			.t("warden_desc", "_守望者_同样会被完全净化，而不会因此沉睡，并能在短时间内免疫所有环境影响。")
			.t("$seed.name", "梦叶花之种");
	}

	/** 旧存档里 {@code pd.plants.Mageroyal$Seed} 的反序列化入口；行为与 {@link Dreamfoil.Seed} 一致。 */
	public static class Seed extends Dreamfoil.Seed {
	}
}
