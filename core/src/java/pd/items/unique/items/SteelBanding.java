/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;

/**
 * SPSEXPD: 柔钢绑带 —— 决斗二档（王冠 B 路线）的副护甲。
 *
 * <p>与符文绑带同源，但_防护能力更高_，同时保留「指定刻印」。</p>
 */
public class SteelBanding extends RuneBanding {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.DUELIST_SHIELD_T2_2_STEEL;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SteelBanding.class)
			.t("name", "柔钢绑带")
			.t("ac_inscribe", "指定刻印")
			.t("prompt_material", "选择要消耗的炼金材料")
			.t("choose_title", "选择要写上的刻印")
			.t("no_material", "你需要一件炼金材料才能这么做。")
			.t("bad_glyph", "这道刻印无法写入。")
			.t("inscribed", "柔钢里的纹路舒展开来，%s被写了上去。")
			.t("desc", "一条用柔钢片编成的绑带，弯折时几乎没有声音。\n\n它只能装备在_副护甲栏_，并像副手专用的盾牌一样_不会造成双持惩罚_。\n\n比起符文绑带，它的_防护能力更强_，同样可以「指定刻印」——_消耗一件炼金材料_，把想好的那一种刻印直接写到自己身上。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SPS_PH_ARMOR_SPARE;
		unique = true;
	}

	//SPSEXPD: 柔钢绑带比布制绑带更耐打
	@Override public int DRMax(int lvl) { return 4 + 2 * lvl; }
	@Override public int DRMin(int lvl) { return 2 + lvl; }
	@Override public int STRReq(int lvl) { return 10; }

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
