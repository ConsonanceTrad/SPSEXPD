/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 英魂宝珠 —— 战士一档（面具 B 路线）的贴附物之一。
 *
 * <p>可贴附到武器或护甲：携带上限 _1 级_，不可携带刻印；贴附后使该装备的双持力量惩罚_减少 2 点_。</p>
 */
public class SoulOrb extends CarrierOrb {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.WARRIOR_SEAL_T1_2_SOUL;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SoulOrb.class)
			.t("name", "英魂宝珠")
			.t("desc", "一颗裹着微光的宝珠，里头似乎封着一点不肯散去的执念。\n\n它可以被_按进武器或护甲_里，替那件装备扛下一小部分双持的代价——贴附后该装备的_双持力量惩罚减少 2 点_。\n\n它能携带最多 _1 级_升级（贴附时一并转移），但_无法携带任何刻印_。");
	}

	@Override public int maxCarriedLevel() { return 1; }

	@Override public int dualWieldReduction() { return 2; }

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
