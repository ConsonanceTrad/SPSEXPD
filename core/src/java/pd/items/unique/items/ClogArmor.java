/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.armor.normalarmor.ClothArmor;
import pd.messages.InlineText;

/**
 * SPSEXPD: 木屐 —— 苦修者开局的两件专属物品之一。
 *
 * <p>特殊副护甲：只能装备在副护甲栏，且不参与双甲规则（不涨力量需求、不降移速）。
 * 装备期间<b>连续移动 5 回合</b>会提高移动速度（+30%），停下脚步即失去加成
 * （见 {@link pd.actors.buffs.ClogMomentum}）。</p>
 */
public class ClogArmor extends ClothArmor {


	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ClogArmor.class)
			.t("name", "木屐")
			.t("desc", "一双用旧木削成的屐齿鞋，走起路来咔咔作响。\n\n它只能装备在_副护甲栏_，并且像副手专用的盾牌一样，_不会造成双持惩罚_。\n\n装备期间只要_连续移动 5 回合_，木屐就会让你走得更轻快（移速 +30%）；一旦停下脚步，这份势头就会消失。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SPS_PH_ARMOR_SPARE;
	}

	/** SPSEXPD: 副手专用护甲——不能装备到主护甲栏。 */
	@Override public boolean canEquipPrimary() { return false; }

	/** SPSEXPD: 不参与双甲规则（不涨力量需求、不降移速）。 */
	@Override public boolean countsForDualArmor() { return false; }

}
