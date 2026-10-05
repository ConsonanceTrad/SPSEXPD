/*
 * 迅捷施法 —— 法杖施法速度更快。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class QuickZap extends Perk {

	static {
		InlineText.of(QuickZap.class)
				.t("title", "迅捷施法")
				.t("desc", "法杖施法的耗时缩短。");
	}

	public QuickZap() {
		super(1);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.WAND_QUICK_ZAP;
	}

	public float speedMultiplier() {
		return 0.65f;
	}
}
