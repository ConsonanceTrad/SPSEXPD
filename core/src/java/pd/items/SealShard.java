/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 纹章碎片 —— 图标为破损纹章的同列下一行（non_equip 图集 col0/row4，0 基）。
 * 与破损纹章同机制（可贴附护甲、可在护甲间转移），但能力被削弱：
 *   · 携带升级上限 1 级（破损纹章为 2 级）
 *   · 只能携带普通刻印（非诅咒；项目里没有「强大」标记，暂不额外排除）
 *   · 不提供战士护盾
 */

package pd.items;

import pd.atlas.items.EquipmentNonEquipDict;
import pd.items.equipment.armor.Armor;
import pd.messages.InlineText;

public class SealShard extends BrokenSeal {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SealShard.class)
			.t("name", "纹章碎片")
			.t("ac_affix", "贴附")
			.t("prompt", "选择一件护甲。")
			.t("unknown_armor", "你需要先鉴定那件护甲有无诅咒。")
			.t("already_affixed", "这件护甲上已经贴附了一枚纹章，不能再贴附第二枚。")
			.t("cursed_armor", "纹章不能贴附于被诅咒的盔甲。")
			.t("affix", "你将纹章碎片佩挂在了护甲上！")
			.t("inscribed", "碎片刻有_%s_。")
			.t("choose_title", "选择一个刻印")
			.t("choose_desc", "这件护甲与纹章碎片均刻有刻印。请选择一个想保留的刻印。\n\n护甲刻印：%1$s\n纹章碎片刻印：%2$s\n\n注意，如果选择保留护甲的刻印，碎片将无法转移该刻印。")
			.t("desc", "破损纹章崩落下来的一角蜡片，其上的防御符咒已在断裂处变得模糊。\n\n和完整的纹章一样，它可以被_贴附在护甲上_并在护甲之间转移，但能承载的力量有限：最多携带 _1 级_升级（升级时碎片需已贴附在护甲上），并且只能携带_普通刻印_——诅咒或强大的刻印无法附着其上。\n\n它_不会_提供战士护盾。取下后同样是一件可被打造的物品：可被_升级卷轴_升级（以 1 级为限），也可被_附魔道具_刻上普通刻印。");
	}

	{
		//SPSEXPD: 引用语义别名而不是坐标名——图集里挪格时这里不用跟着改
		image = EquipmentNonEquipDict.SEAL_FRAGMENT;
	}

	@Override
	public int maxCarriedLevel() {
		return 1;
	}

	@Override
	public boolean canCarryGlyph( Armor.Glyph g ) {
		//SPSEXPD: 只允许普通刻印（非诅咒）
		return g != null && !g.curse();
	}

	@Override
	public int maxShield( int armTier, int armLvl ) {
		//SPSEXPD: 碎片不提供护盾
		return 0;
	}
}
