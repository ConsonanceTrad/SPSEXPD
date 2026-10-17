/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 奥法古籍 —— 法师二档（王冠 A 路线）的特殊法杖。
 *
 * <p>与奥法轴芯同源，但容量大得多：可收录最多 {@link #MAX_SPELLS} 道法术。</p>
 */
public class ArcaneTome extends ArcaneCore {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.MAGE_STAFF_T2_1_TOME;

	public static final int MAX_SPELLS = 9;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ArcaneTome.class)
			.t("name", "奥法古籍")
			.t("ac_switch", "切换法术")
			.t("swallow_prompt", "选择要吞噬的法杖")
			.t("swallowed", "古籍吞下了%s，收录了这道法术（%d/%d）。")
			.t("full", "古籍已经收录了 %d 道法术，无法再吞下更多。")
			.t("switch_title", "选择要使用的法术")
			.t("no_switch", "古籍里只收录了一道法术，没什么可切换的。")
			.t("switched", "古籍翻到了%s的那一页。")
			.t("desc", "一本会自己翻页的古籍，摊开时像一根法杖。\n\n「吞噬」：把法杖喂给它——法杖本体消融，只留下那道_法术_被收录。最多收录 _%1$d 道_法术。\n\n「切换法术」：在已收录的法术之间自由切换，想用哪道就用哪道。\n\n它的_能量上限不看等级_：基础 _2 点_，每收录一道法术 _+2 点_。_充能速度_也随收录数提高：空着时约 _30 回合_回 1 点，每多收录一道快 _15%%_。\n\n吞噬时与老法杖灌注同规则：会继承比自己等级更高的法杖等级，等级相等时额外获得一次_免费升级_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	protected int maxSpells() {
		return MAX_SPELLS;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
