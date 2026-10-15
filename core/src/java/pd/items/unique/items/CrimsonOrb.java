/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 赤血宝珠 —— 战士二档（王冠 B 路线）的贴附物之一。
 *
 * <p>可贴附到武器或护甲：携带上限 _4 级_，不可携带刻印；贴附后_完全免除_该装备的双持力量惩罚。</p>
 */
public class CrimsonOrb extends CarrierOrb {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.WARRIOR_SEAL_T2_2_BLOOD;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(CrimsonOrb.class)
			.t("name", "赤血宝珠")
			.t("desc", "一颗红得发黑的宝珠，按上去时能感到它在替你分担重量。\n\n它可以被_按进武器或护甲_里，让那件装备的_双持力量惩罚彻底消失_——你不再需要为同时带着它付出额外的力量。\n\n它能携带最多 _4 级_升级（贴附时一并转移），但_无法携带任何刻印_。");
	}

	@Override public int maxCarriedLevel() { return 4; }

	@Override public int dualWieldReduction() { return -1; }

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
