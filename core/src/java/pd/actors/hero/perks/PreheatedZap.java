/*
 * 预热施法 —— 施法失败后，下一发必定暴击。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class PreheatedZap extends Perk {

	static {
		InlineText.of(PreheatedZap.class)
				.t("title", "预热施法")
				.t("desc", "施法失败后，你的下一次法术施放必定暴击。");
	}

	public PreheatedZap() {
		super(1);
		addTags(Tag.Wand);
	}

	@Override
	public int image() {
		return PerkImageSheet.PREHEATED_ZAP;
	}
}
