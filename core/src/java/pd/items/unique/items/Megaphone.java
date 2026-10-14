/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.misc.GauntletShield;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 扩音筒 —— 演员开局的第二件专属物品（特殊副武器），占位实现。
 *
 * <p>沿用「副手专用、不可投掷」的护手盾骨架；具体效果尚未实装。基础素质与关系见
 * {@link UniqueItemId#PERFORMER_SHOVEL_BASE_B}。</p>
 */
public class Megaphone extends GauntletShield implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_BASE_B;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Megaphone.class)
			.t("name", "扩音筒")
			.t("desc", "一只蒙了锈的铁皮喇叭，嗓门大得能把嗓子喊哑。它目前只是一件占位副武器，还没有任何实际效果。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	/** SPSEXPD: 占位，不显示护手盾的格挡数值。
	 *  注意必须返回空串而不是 null —— MeleeWeapon.info() 会对 statsInfo() 直接调 .equals("")。 */
	@Override public String statsInfo() { return ""; }

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
