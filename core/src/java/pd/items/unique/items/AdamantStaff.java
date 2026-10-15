/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 精金手杖 —— 法师二档（王冠 B 路线）的近战特殊武器。
 *
 * <p>与秘银手杖同源，但每次都为你_全部_的法杖补充能量，同样带上次施法的完整命中效果。</p>
 */
public class AdamantStaff extends MythrilStaff {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.MAGE_STAFF_T2_2_ADAMANT;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(AdamantStaff.class)
			.t("name", "精金手杖")
			.t("desc", "一根通体精金的实心手杖——它_没法施法_，只能拿来打人。\n\n每次近战命中，它会为你身上_全部_的法杖各补上一点能量；同时，你_上次施放的法术_会顺着杖身涌出，让这一击带上那道法术的完整命中效果。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	/** SPSEXPD: 覆盖全部法杖。 */
	@Override
	protected int rechargeCount(int total) {
		return total;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
