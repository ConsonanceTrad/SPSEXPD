/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 荣耀宝珠 —— 战士二档（王冠 B 路线）的镶嵌物之一。
 *
 * <p>可镶嵌到武器或护甲：携带上限 _4 级_，不可携带刻印；镶嵌后_完全免除_该装备的双持力量惩罚。</p>
 */
public class GloryOrb extends CarrierOrb {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.WARRIOR_SEAL_T2_2_GLORY;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(GloryOrb.class)
			.t("name", "荣耀宝珠")
			.t("desc", "一颗泛着金色内光的宝珠，凑近时能听见极轻的号角声。\n\n它可以被_镶嵌进武器或护甲_里，让那件装备的_双持力量惩罚彻底消失_，仿佛那份荣耀替你承担了分量。\n\n它能携带最多 _4 级_升级（镶嵌时一并转移），但_无法携带任何刻印_。");
	}

	@Override public int maxCarriedLevel() { return 4; }

	@Override public int dualWieldReduction() { return -1; }

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
