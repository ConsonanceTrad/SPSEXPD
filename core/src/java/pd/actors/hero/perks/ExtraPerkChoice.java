/*
 * 额外特质位 —— 升级选择特质时的候选数由 3 提升到 5。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class ExtraPerkChoice extends Perk {

	static {
		InlineText.of(ExtraPerkChoice.class)
				.t("title", "额外特质位")
				.t("desc", "选择特质时的候选数量由 3 个提升到 5 个。");
	}

	public ExtraPerkChoice() {
		super(1);
	}

	@Override
	public int image() {
		return PerkImageSheet.EXTRA_CHOICE;
	}
}
