/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.armor.normalarmor.ClothArmor;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 木屐 —— 苦修者开局的第二件专属物品（副护甲），占位实现。
 *
 * <p>数值暂时沿用布甲；具体效果尚未实装。基础素质与关系见
 * {@link UniqueItemId#ASCETIC_EARRING_BASE_B}。</p>
 */
public class ClogArmor extends ClothArmor implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_BASE_B;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ClogArmor.class)
			.t("name", "木屐")
			.t("desc", "一双用旧木削成的屐齿鞋，走起路来咔咔作响。它目前只是一件占位护甲，还没有任何实际效果。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SPS_PH_ARMOR_SPARE;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
