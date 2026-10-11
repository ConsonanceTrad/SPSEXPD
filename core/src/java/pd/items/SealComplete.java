/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 完整纹章 —— 破损纹章被天狗面具强化后的成品形态。
 * 与破损纹章同机制（可贴附护甲、可在护甲间转移、携带任意刻印），但各项都更强：
 *   · 携带升级上限 5 级（破损纹章 2 级、碎片 1 级）
 *   · 护盾上限比破损纹章多 4 点
 *   · 可携带任意刻印
 */

package pd.items;

import pd.atlas.items.EquipmentNonEquipDict;
import pd.messages.InlineText;

public class SealComplete extends BrokenSeal {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SealComplete.class)
			.t("name", "完整纹章")
			.t("ac_affix", "贴附")
			.t("prompt", "选择一件护甲。")
			.t("unknown_armor", "你需要先鉴定那件护甲有无诅咒。")
			.t("already_affixed", "这件护甲上已经贴附了一枚纹章，不能再贴附第二枚。")
			.t("cursed_armor", "纹章不能贴附于被诅咒的盔甲。")
			.t("affix", "你将完整纹章佩挂在了护甲上！")
			.t("inscribed", "纹章刻有_%s_。")
			.t("choose_title", "选择一个刻印")
			.t("choose_desc", "这件护甲与完整纹章均刻有刻印。请选择一个想保留的刻印。\n\n护甲刻印：%1$s\n完整纹章刻印：%2$s\n\n注意，如果选择保留护甲的刻印，纹章将无法转移该刻印。")
			.t("desc", "天狗面具把破损纹章上的裂痕重新熔合，磨损的符咒也随之变得完整而明亮。\n\n它比破损纹章更强：护盾随时间累积（_每 7 回合_获得上限 _20%%_ 的护盾，至少 1 点），上限为 _护甲等级 + 阶位×2 + 4_；极度饥饿时暂停累积。\n\n它可以携带最多 _5 级_升级（升级时纹章需已贴附在护甲上），并能携带_任意刻印_。\n\n纹章_取下后_同样是一件可被打造的物品：它可以被_升级卷轴_继续升级（以 5 级为限），也可以被_附魔道具_刻上新的刻印。");
	}

	{
		//SPSEXPD: 引用语义别名而不是坐标名——图集里挪格时这里不用跟着改
		image = EquipmentNonEquipDict.SEAL_COMPLETE;
	}

	/** SPSEXPD: 成品形态，携带上限最高的一档。 */
	@Override
	public int maxCarriedLevel() {
		return 5;
	}

	@Override
	public int maxShield( int armTier, int armLvl ) {
		//SPSEXPD: 比破损纹章（armLvl + armTier*2）多 4 点上限
		return armLvl + armTier * 2 + 4;
	}
}
