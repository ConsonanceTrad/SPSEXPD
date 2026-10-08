/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.messages.InlineText;

/**
 * SPSEXPD: 旧存档壳——「未祝福的十字架」概念已随安卡改造删除。
 * 安卡不再需要祝福，本类只为兼容旧存档里已存在的物品实例，
 * 行为完全等同 {@link Ankh}（旧的掉落点与挑战奖励现在直接发放安卡）。
 */
public class UnBlessAnkh extends Ankh {
	//SPSEXPD: 旧存档里的「十字架」实例统一显示为安卡
	static {
		InlineText.of(UnBlessAnkh.class)
			.t("name", "安卡");
	}
}
