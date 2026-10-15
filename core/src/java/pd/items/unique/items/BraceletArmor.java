/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.armor.normalarmor.ClothArmor;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 护手绑带 —— 决斗家开局的两件专属物品之一。
 *
 * <p>特殊副护甲：只能装备在副护甲栏，且与副手专用的神木圆盾同理，
 * <b>不参与双甲规则</b>——不会让主/副护甲的力量需求 +50%，也不会带来副甲的速度惩罚。</p>
 */
public class BraceletArmor extends ClothArmor implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.DUELIST_SHIELD_BASE_B;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(BraceletArmor.class)
			.t("name", "护手绑带")
			.t("desc", "一条缠在护手内侧的加固布带，用来在副手替下盾牌。\n\n它只能装备在_副护甲栏_，并且像副手专用的盾牌一样，_不会造成双持惩罚_（不增加护甲力量需求，也不会拖慢你的速度）。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SPS_PH_ARMOR_SPARE;
	}

	/** SPSEXPD: 副手专用护甲——不能装备到主护甲栏。 */
	@Override public boolean canEquipPrimary() { return false; }

	/** SPSEXPD: 不参与双甲规则（不涨力量需求、不降移速）。 */
	@Override public boolean countsForDualArmor() { return false; }

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
