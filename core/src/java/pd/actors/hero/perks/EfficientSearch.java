/*
 * 高效搜索 —— 搜索范围翻倍（盗贼的「搜索更远」由本特质体现）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class EfficientSearch extends Perk {

	static {
		InlineText.of(EfficientSearch.class)
				.t("title", "高效搜索")
				.t("desc", "搜索半径增加 1 格，能搜索到更远处的隐藏门与陷阱。");
	}

	public EfficientSearch() {
		super(1);
	}

	@Override
	public int image() {
		return PerkImageSheet.SEARCH_EFFICIENT;
	}

	/** 额外搜索半径 */
	public int extraRange() {
		return 1;
	}
}
