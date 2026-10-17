/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 鲜血宝珠 —— 战士一档（面具 B 路线）的镶嵌物之一。
 *
 * <p>可镶嵌到武器或护甲：携带上限 _1 级_，不可携带刻印；镶嵌后使该装备的双持力量惩罚_减少 2 点_。</p>
 */
public class BloodOrb extends CarrierOrb {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.WARRIOR_SEAL_T1_2_BLOOD;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(BloodOrb.class)
			.t("name", "鲜血宝珠")
			.t("desc", "一颗凝着暗红血珠的宝珠，按上去时能听见极轻的一声咽。\n\n它可以被_镶嵌进武器或护甲_里，用血替你抵掉一小部分双持的代价——镶嵌后该装备的_双持力量惩罚减少 2 点_。\n\n它能携带最多 _1 级_升级（镶嵌时一并转移），但_无法携带任何刻印_。");
	}

	@Override public int maxCarriedLevel() { return 1; }

	@Override public int dualWieldReduction() { return 2; }

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
